package com.delivery.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.delivery.model.Pedido;
import com.delivery.model.Produto;
import com.delivery.service.PedidoService;
import com.delivery.service.PessoaService;

@Controller
@RequestMapping("/pedido")
public class PedidoController {

	@Autowired
	private PedidoService pedidoService;
	
	@Autowired
	private PessoaService pessoaService;
	
	private static List<Produto> listaPedido = new ArrayList<Produto>();  
	
	public static void adicionarProdutoAoPedido(Produto produto) {
		listaPedido.add(produto);
	}
	
	public static void limparCarrinho() {
		listaPedido.clear();
	}
	
	public float getValorTotalCarrinho() {
		float total = 0;
		for (Produto produto : listaPedido) {
			total += produto.getPreco();
		}
		return total;
	}
	
	@RequestMapping("/listar")
	public ModelAndView listarPedidos() {
		ModelAndView mv = new ModelAndView("listaPedido");
		mv.addObject("listarProdutosPedido",listaPedido);
		mv.addObject("valorTotal", getValorTotalCarrinho());
		return mv;
	}
	
	@RequestMapping("/confirmar")
	public ModelAndView confirmarPedido(@RequestParam(value = "enderecoPedido") String endereco) {
		Pedido pedido = new Pedido();
		pedido.setEnderecoPedido(endereco);
		pedido.setValorTotal(getValorTotalCarrinho());
		pedido.setCodigoCliente(pessoaService.getPessoaSession().getCodigo());
		String pratoPedido = "";
		for (Produto produto : listaPedido) {
			pratoPedido += produto.getNomeProduto() + "," + "R$" + produto.getPreco() + "\n";
		}
		System.out.println(pratoPedido);
		pedido.setPratosPedido(pratoPedido);
		pedidoService.cadastrar(pedido);
		ModelAndView mv = new ModelAndView("index");
		limparCarrinho();
		return mv;
	}
	
	@RequestMapping("/excluir/{idProduto}")
	public ModelAndView excluirPratoDoPedido(@PathVariable("idProduto") Long codigo) {
		for (Produto produto : listaPedido) {
			if(produto.getIdProduto() == codigo) {
				listaPedido.remove(produto);
				break;
			}
		}
		ModelAndView mv = new ModelAndView("redirect:/pedido/listar");
		return mv;
	}
	
}
