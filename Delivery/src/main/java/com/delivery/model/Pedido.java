package com.delivery.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Pedido {
	@Id
	@GeneratedValue (strategy = GenerationType.AUTO)
	private Long codigoPedido;
	
	private Long codigoPessoa;
	private float valorTotal;
	private String enderecoPedido;
	private String pratosPedido;
	
	public String getPratosPedido() {
		return pratosPedido;
	}

	public void setPratosPedido(String pratosPedido) {
		this.pratosPedido = pratosPedido;
	}

	public Pedido() {}
	
	public String getEnderecoPedido() {
		return enderecoPedido;
	}


	public void setEnderecoPedido(String enderecoPedido) {
		this.enderecoPedido = enderecoPedido;
	}


	public Long getCodigoPedido() {
		return codigoPedido;
	}


	public void setCodigoPedido(Long codigoPedido) {
		this.codigoPedido = codigoPedido;
	}


	public Long getCodigoPessoa() {
		return codigoPessoa;
	}


	public void setCodigoCliente(Long codigoPessoa) {
		this.codigoPessoa = codigoPessoa;
	}

	public float getValorTotal() {
		return valorTotal;
	}


	public void setValorTotal(float valorTotal) {
		this.valorTotal = valorTotal;
	}
	
	
}
