package com.delivery.service;

import com.delivery.dto.EntregaRequestDTO;
import com.delivery.dto.EntregaResponseDTO;
import com.delivery.exception.UserNotFoundException;
import com.delivery.mapper.EntregaMapper;
import com.delivery.model.Entrega;
import com.delivery.model.Pedido;
import com.delivery.model.StatusEntrega;
import com.delivery.model.Usuario;
import com.delivery.repository.EntregaRepository;
import com.delivery.repository.PedidoRepository;
import com.delivery.repository.UsuarioRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EntregaService {

    private static final String ROLE_DELIVERY = "ROLE_DELIVERY";
    private static final String ROLE_ADMIN = "ROLE_ADMIN";

    private final EntregaRepository entregaRepository;
    private final PedidoRepository pedidoRepository;
    private final UsuarioRepository usuarioRepository;
    private final EntregaMapper entregaMapper;
    private final WebSocketService webSocketService;

    public EntregaService(EntregaRepository entregaRepository, PedidoRepository pedidoRepository,
                          UsuarioRepository usuarioRepository, EntregaMapper entregaMapper,
                          WebSocketService webSocketService) {
        this.entregaRepository = entregaRepository;
        this.pedidoRepository = pedidoRepository;
        this.usuarioRepository = usuarioRepository;
        this.entregaMapper = entregaMapper;
        this.webSocketService = webSocketService;
    }

    public EntregaResponseDTO criarEntrega(EntregaRequestDTO entregaDTO) {
        Pedido pedido = pedidoRepository.findById(entregaDTO.getCodigoPedido())
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado."));

        if (pedido.getEntrega() != null) {
            throw new IllegalStateException("Este pedido já possui uma entrega associada.");
        }

        Entrega entrega = entregaMapper.toEntity(entregaDTO);
        entrega.setPedido(pedido);
        entrega.setStatus(StatusEntrega.PENDENTE);
        entrega.setCriadoEm(new Date());

        Entrega entregaSalva = entregaRepository.save(entrega);
        webSocketService.notifyDeliveryAvailable(entregaSalva);
        return entregaMapper.toResponseDTO(entregaSalva);
    }

    public List<EntregaResponseDTO> listarEntregasDisponiveis() {
        return entregaRepository.findByStatusAndEntregadorIsNull(StatusEntrega.PENDENTE).stream()
                .map(entregaMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public EntregaResponseDTO aceitarEntrega(Long id) {
        Entrega entrega = entregaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Entrega não encontrada."));

        if (entrega.getStatus() != StatusEntrega.PENDENTE || entrega.getEntregador() != null) {
            throw new IllegalStateException("Esta entrega não está disponível para ser aceita.");
        }

        Usuario entregador = getAuthenticatedUser();
        if (!isUsuarioComRole(entregador, ROLE_DELIVERY)) {
            throw new SecurityException("Usuário não autorizado a aceitar entregas.");
        }

        entrega.setEntregador(entregador);
        entrega.setStatus(StatusEntrega.ACEITA);
        Entrega entregaAtualizada = entregaRepository.save(entrega);
        webSocketService.notifyDeliveryStatusUpdate(entregaAtualizada);
        return entregaMapper.toResponseDTO(entregaAtualizada);
    }

    public EntregaResponseDTO atualizarStatusEntrega(Long id, StatusEntrega novoStatus) {
        Entrega entrega = entregaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Entrega não encontrada."));

        Usuario usuarioLogado = getAuthenticatedUser();
        validarAutorizacaoParaAtualizarStatus(entrega, usuarioLogado);
        validarTransicaoStatus(entrega.getStatus(), novoStatus);

        entrega.setStatus(novoStatus);
        if (novoStatus == StatusEntrega.ENTREGUE) {
            entrega.setEntregueEm(new Date());
        }

        Entrega entregaAtualizada = entregaRepository.save(entrega);

        webSocketService.notifyDeliveryStatusUpdate(entregaAtualizada);

        return entregaMapper.toResponseDTO(entregaAtualizada);
    }

    public List<EntregaResponseDTO> listarMinhasEntregas() {
        Usuario entregador = getAuthenticatedUser();
        if (!isUsuarioComRole(entregador, ROLE_DELIVERY)) {
            throw new SecurityException("Usuário não é um entregador.");
        }
        return entregaRepository.findByEntregador(entregador).stream()
                .map(entregaMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    private void validarAutorizacaoParaAtualizarStatus(Entrega entrega, Usuario usuarioLogado) {
        boolean isAdmin = isUsuarioComRole(usuarioLogado, ROLE_ADMIN);
        boolean isOwner = entrega.getEntregador() != null && entrega.getEntregador().getCodigo().equals(usuarioLogado.getCodigo());

        if (!isAdmin && !isOwner) {
            throw new SecurityException("Usuário não autorizado a atualizar o status desta entrega.");
        }
    }

    private void validarTransicaoStatus(StatusEntrega statusAtual, StatusEntrega novoStatus) {
        if (statusAtual == novoStatus) {
            return; // Nenhuma alteração
        }

        switch (statusAtual) {
            case PENDENTE:
                if (novoStatus != StatusEntrega.ACEITA) {
                    throw new IllegalStateException("Não é possível alterar o status de PENDENTE para " + novoStatus);
                }
                break;
            case ACEITA:
                if (novoStatus != StatusEntrega.COLETADA) {
                    throw new IllegalStateException("Não é possível alterar o status de ACEITA para " + novoStatus);
                }
                break;
            case COLETADA:
                if (novoStatus != StatusEntrega.EM_ROTA) {
                    throw new IllegalStateException("Não é possível alterar o status de COLETADA para " + novoStatus);
                }
                break;
            case EM_ROTA:
                if (novoStatus != StatusEntrega.ENTREGUE) {
                    throw new IllegalStateException("Não é possível alterar o status de EM_ROTA para " + novoStatus);
                }
                break;
            case ENTREGUE:
            case CANCELADA:
                throw new IllegalStateException("Não é possível alterar o status de uma entrega finalizada ou cancelada.");
        }
    }

    private boolean isUsuarioComRole(Usuario usuario, String role) {
        return usuario.getRoles().stream().anyMatch(r -> r.getAuthority().equals(role));
    }

    private Usuario getAuthenticatedUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioRepository.findByEmail(email);
        if (usuario == null) {
            throw new UserNotFoundException("Inconsistência de dados: usuário autenticado não encontrado: " + email);
        }
        return usuario;
    }
}
