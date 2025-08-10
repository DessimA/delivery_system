package com.delivery.service;

import com.delivery.dto.EntregaRequestDTO;
import com.delivery.dto.EntregaResponseDTO;
import com.delivery.mapper.EntregaMapper;
import com.delivery.model.Entrega;
import com.delivery.model.Pessoa;
import com.delivery.model.Pedido;
import com.delivery.repository.EntregaRepository;
import com.delivery.repository.PessoaRepository;
import com.delivery.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EntregaService {

    @Autowired
    private EntregaRepository entregaRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private PessoaRepository pessoaRepository;

    @Autowired
    private EntregaMapper entregaMapper;

    @Autowired
    private SimpMessagingTemplate messagingTemplate; // Injetar SimpMessagingTemplate

    public EntregaResponseDTO criarEntrega(EntregaRequestDTO entregaDTO) {
        // Lógica para criar uma nova entrega
        // Pedido deve existir e não ter entrega associada
        Pedido pedido = pedidoRepository.findById(entregaDTO.getCodigoPedido())
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado."));

        if (pedido.getEntrega() != null) {
            throw new IllegalStateException("Este pedido já possui uma entrega associada.");
        }

        Entrega entrega = entregaMapper.toEntity(entregaDTO);
        entrega.setPedido(pedido);
        entrega.setStatus("PENDENTE"); // Status inicial
        entrega.setCriadoEm(new Date());

        Entrega entregaSalva = entregaRepository.save(entrega);
        return entregaMapper.toResponseDTO(entregaSalva);
    }

    public List<EntregaResponseDTO> listarEntregasDisponiveis() {
        // Entregas com status PENDENTE e sem entregador atribuído
        return entregaRepository.findAll().stream()
                .filter(e -> "PENDENTE".equals(e.getStatus()) && e.getEntregador() == null)
                .map(entregaMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public EntregaResponseDTO aceitarEntrega(Long id) {
        Entrega entrega = entregaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Entrega não encontrada."));

        if (!"PENDENTE".equals(entrega.getStatus()) || entrega.getEntregador() != null) {
            throw new IllegalStateException("Esta entrega não está disponível para ser aceita.");
        }

        Pessoa entregador = getAuthenticatedUser();
        if (!entregador.getRoles().stream().anyMatch(r -> r.getPapel().equals("ROLE_DELIVERY"))) {
            throw new SecurityException("Usuário não autorizado a aceitar entregas.");
        }

        entrega.setEntregador(entregador);
        entrega.setStatus("ACEITA");
        Entrega entregaAtualizada = entregaRepository.save(entrega);
        return entregaMapper.toResponseDTO(entregaAtualizada);
    }

    public EntregaResponseDTO atualizarStatusEntrega(Long id, String novoStatus) {
        Entrega entrega = entregaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Entrega não encontrada."));

        Pessoa usuarioLogado = getAuthenticatedUser();

        // Somente ADMIN ou o próprio entregador podem atualizar o status
        boolean isAdmin = usuarioLogado.getRoles().stream().anyMatch(r -> r.getPapel().equals("ROLE_ADMIN"));
        boolean isEntregador = usuarioLogado.getRoles().stream().anyMatch(r -> r.getPapel().equals("ROLE_DELIVERY"));

        if (!isAdmin && (!isEntregador || entrega.getEntregador() == null || !entrega.getEntregador().getCodigo().equals(usuarioLogado.getCodigo()))) {
            throw new SecurityException("Usuário não autorizado a atualizar o status desta entrega.");
        }

        // Validações de transição de status (ex: PENDENTE -> ACEITA -> EM_ROTA -> ENTREGUE)
        // Simplificado por enquanto
        entrega.setStatus(novoStatus);
        if ("ENTREGUE".equals(novoStatus)) {
            entrega.setEntregueEm(new Date());
        }

        Entrega entregaAtualizada = entregaRepository.save(entrega);

        // Enviar notificação WebSocket para o cliente do pedido
        // Tópico específico para o pedido: /topic/pedidos/{codigoPedido}
        if (entregaAtualizada.getPedido() != null && entregaAtualizada.getPedido().getCodigoPedido() != null) {
            messagingTemplate.convertAndSend("/topic/pedidos/" + entregaAtualizada.getPedido().getCodigoPedido(),
                    "Status do seu pedido " + entregaAtualizada.getPedido().getCodigoPedido() + " atualizado para: " + entregaAtualizada.getStatus());
        }

        // Enviar notificação geral para entregadores (se for relevante, ex: nova entrega disponível)
        // messagingTemplate.convertAndSend("/topic/entregas/disponiveis", "Nova entrega disponível!");

        return entregaMapper.toResponseDTO(entregaAtualizada);
    }

    private Pessoa getAuthenticatedUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userEmail;
        if (principal instanceof UserDetails) {
            userEmail = ((UserDetails)principal).getUsername();
        } else {
            userEmail = principal.toString();
        }
        Pessoa pessoa = pessoaRepository.findByEmail(userEmail);
        if (pessoa == null) {
            throw new IllegalStateException("Usuário autenticado não encontrado no banco de dados.");
        }
        return pessoa;
    }

    public List<EntregaResponseDTO> listarMinhasEntregas() {
        Pessoa entregador = getAuthenticatedUser();
        if (!entregador.getRoles().stream().anyMatch(r -> r.getPapel().equals("ROLE_DELIVERY"))) {
            throw new SecurityException("Usuário não é um entregador.");
        }
        return entregaRepository.findByEntregador(entregador).stream()
                .map(entregaMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
}
