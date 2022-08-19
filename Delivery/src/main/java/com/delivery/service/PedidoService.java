package com.delivery.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.delivery.model.Pedido;
import com.delivery.repository.PedidoRepository;


@Service
public class PedidoService {

	@Autowired
	private PedidoRepository pedidoRepository;
	
	public void cadastrar(Pedido pedido) {
		pedidoRepository.save(pedido);
	}
	
	public List<Pedido> listarPedidosPorId(Long pessoaID){
		List<Pedido> pessoaPedidos = new ArrayList<Pedido>();
		for(Pedido pedido : pedidoRepository.findAll()) {
			if(pedido.getCodigoPessoa() == pessoaID) {
				pessoaPedidos.add(pedido);
			}
		}
		return pessoaPedidos;
	}
	
	public void excluir(Long codigo) {
		pedidoRepository.deleteById(codigo);
	}
	
	public Pedido buscar(Long codigo) {
		return pedidoRepository.getOne(codigo);
	}
	
}
