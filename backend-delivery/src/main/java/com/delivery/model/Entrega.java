package com.delivery.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
public class Entrega {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "pedido_id", referencedColumnName = "codigoPedido")
    private Pedido pedido;

    @ManyToOne
    @JoinColumn(name = "entregador_id", referencedColumnName = "codigo")
    private Usuario entregador;

    private String enderecoOrigem;
    private String enderecoDestino;

    @Enumerated(EnumType.STRING)
    private StatusEntrega status;

    private Integer tempoEstimado;

    private BigDecimal valorEntrega;

    @Temporal(TemporalType.TIMESTAMP)
    private Date criadoEm;

    @Temporal(TemporalType.TIMESTAMP)
    private Date entregueEm;

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    public Usuario getEntregador() {
        return entregador;
    }

    public void setEntregador(Usuario entregador) {
        this.entregador = entregador;
    }

    public String getEnderecoOrigem() {
        return enderecoOrigem;
    }

    public void setEnderecoOrigem(String enderecoOrigem) {
        this.enderecoOrigem = enderecoOrigem;
    }

    public String getEnderecoDestino() {
        return enderecoDestino;
    }

    public void setEnderecoDestino(String enderecoDestino) {
        this.enderecoDestino = enderecoDestino;
    }

    public StatusEntrega getStatus() {
        return status;
    }

    public void setStatus(StatusEntrega status) {
        this.status = status;
    }

    public Integer getTempoEstimado() {
        return tempoEstimado;
    }

    public void setTempoEstimado(Integer tempoEstimado) {
        this.tempoEstimado = tempoEstimado;
    }

    public BigDecimal getValorEntrega() {
        return valorEntrega;
    }

    public void setValorEntrega(BigDecimal valorEntrega) {
        this.valorEntrega = valorEntrega;
    }

    public Date getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Date criadoEm) {
        this.criadoEm = criadoEm;
    }

    public Date getEntregueEm() {
        return entregueEm;
    }

    public void setEntregueEm(Date entregueEm) {
        this.entregueEm = entregueEm;
    }
}
