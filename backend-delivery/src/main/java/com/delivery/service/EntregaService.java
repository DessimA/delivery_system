package com.delivery.service;

import com.delivery.dto.EntregaRequestDTO;
import com.delivery.dto.EntregaResponseDTO;
import com.delivery.mapper.EntregaMapper;
import com.delivery.model.Entrega;
import com.delivery.model.Pedido;
import com.delivery.model.Pessoa;
import com.delivery.repository.EntregaRepository;
import com.delivery.repository.PedidoRepository;
import com.delivery.repository.PessoaRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EntregaService {

    // Constants to avoid magic strings
    private static final String STATUS_PENDENTE = "PENDENTE";
    private static final String STATUS_ACEITA = "ACEITA";
    private static final String STATUS_ENTREGUE = "ENTREGUE";
    private static final String ROLE_DELIVERY = "ROLE_DELIVERY";
    private static final String ROLE_ADMIN = "ROLE_ADMIN";

    private final EntregaRepository entregaRepository;
    private final PedidoRepository pedidoRepository;
    private final PessoaRepository pessoaRepository;
    private final EntregaMapper entregaMapper;
    private final SimpMessagingTemplate messagingTemplate;

    public EntregaService(EntregaRepository entregaRepository, PedidoRepository pedidoRepository,
                          PessoaRepository pessoaRepository, EntregaMapper entregaMapper,
                          SimpMessagingTemplate messagingTemplate) {
        this.entregaRepository = entregaRepository;
        this.pedidoRepository = pedidoRepository;
        this.pessoaRepository = pessoaRepository;
        this.entregaMapper = entregaMapper;
        this.messagingTemplate = messagingTemplate;
    }

    public EntregaResponseDTO criarEntrega(EntregaRequestDTO entregaDTO) {
        Pedido pedido = pedidoRepository.findById(entregaDTO.getCodigoPedido())
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado."));

        if (pedido.getEntrega() != null) {
            throw new IllegalStateException("Este pedido já possui uma entrega associada.");
        }

        Entrega entrega = entregaMapper.toEntity(entregaDTO);
        entrega.setPedido(pedido);
        entrega.setStatus(STATUS_PENDENTE);
        entrega.setCriadoEm(new Date());

        Entrega entregaSalva = entregaRepository.save(entrega);
        return entregaMapper.toResponseDTO(entregaSalva);
    }

    public List<EntregaResponseDTO> listarEntregasDisponiveis() {
        // TODO: Otimizar esta query. Em vez de buscar tudo e filtrar na memória,
        // criar um método no repository: findByStatusAndEntregadorIsNull(String status)
        return entregaRepository.findAll().stream()
                .filter(e -> STATUS_PENDENTE.equals(e.getStatus()) && e.getEntregador() == null)
                .map(entregaMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    // TODO: Usar anotações de segurança do Spring para validação de role. Ex: @PreAuthorize("hasRole('DELIVERY')")
    public EntregaResponseDTO aceitarEntrega(Long id) {
        Entrega entrega = entregaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Entrega não encontrada."));

        if (!STATUS_PENDENTE.equals(entrega.getStatus()) || entrega.getEntregador() != null) {
            throw new IllegalStateException("Esta entrega não está disponível para ser aceita.");
        }

        Pessoa entregador = getAuthenticatedUser();
        if (!isUsuarioComRole(entregador, ROLE_DELIVERY)) {
            throw new SecurityException("Usuário não autorizado a aceitar entregas.");
        }

        entrega.setEntregador(entregador);
        entrega.setStatus(STATUS_ACEITA);
        Entrega entregaAtualizada = entregaRepository.save(entrega);
        return entregaMapper.toResponseDTO(entregaAtualizada);
    }

    public EntregaResponseDTO atualizarStatusEntrega(Long id, String novoStatus) {
        Entrega entrega = entregaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Entrega não encontrada."));

        Pessoa usuarioLogado = getAuthenticatedUser();
        validarAutorizacaoParaAtualizarStatus(entrega, usuarioLogado);

        entrega.setStatus(novoStatus);
        if (STATUS_ENTREGUE.equals(novoStatus)) {
            entrega.setEntregueEm(new Date());
        }

        Entrega entregaAtualizada = entregaRepository.save(entrega);

        enviarNotificacaoDeStatus(entregaAtualizada);

        return entregaMapper.toResponseDTO(entregaAtualizada);
    }

    public List<EntregaResponseDTO> listarMinhasEntregas() {
        Pessoa entregador = getAuthenticatedUser();
        if (!isUsuarioComRole(entregador, ROLE_DELIVERY)) {
            throw new SecurityException("Usuário não é um entregador.");
        }
        return entregaRepository.findByEntregador(entregador).stream()
                .map(entregaMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    private void validarAutorizacaoParaAtualizarStatus(Entrega entrega, Pessoa usuarioLogado) {
        boolean isAdmin = isUsuarioComRole(usuarioLogado, ROLE_ADMIN);
        boolean isOwner = entrega.getEntregador() != null && entrega.getEntregador().getCodigo().equals(usuarioLogado.getCodigo());

        if (!isAdmin && !isOwner) {
            throw new SecurityException("Usuário não autorizado a atualizar o status desta entrega.");
        }
    }

    private void enviarNotificacaoDeStatus(Entrega entrega) {
        if (entrega.getPedido() != null && entrega.getPedido().getCodigoPedido() != null) {
            String topico = "/topic/pedidos/" + entrega.getPedido().getCodigoPedido();
            String mensagem = String.format("Status do seu pedido %d atualizado para: %s",
                    entrega.getPedido().getCodigoPedido(), entrega.getStatus());
            messagingTemplate.convertAndSend(topico, mensagem);
        }
    }

    private boolean isUsuarioComRole(Pessoa pessoa, String role) {
        return pessoa.getRoles().stream().anyMatch(r -> r.getAuthority().equals(role));
    }

    private Pessoa getAuthenticatedUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Pessoa pessoa = pessoaRepository.findByEmail(email);
        if (pessoa == null) {
            throw new IllegalStateException("Inconsistência de dados: usuário autenticado não encontrado: " + email);
        }
        return pessoa;
    }
}
