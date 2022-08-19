package com.delivery.controller;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.servlet.ModelAndView;

import com.delivery.model.Pedido;
import com.delivery.model.Pessoa;
import com.delivery.service.PedidoService;
import com.delivery.service.PessoaService;

@Controller
@RequestMapping("/pessoa")
public class PessoaController {

	@Autowired
	private PessoaService pessoaService;
	
	@Autowired
	private PedidoService pedidoService;
	
	@RequestMapping("/logar")
	public ModelAndView formLogin() {
		ModelAndView mv = new ModelAndView("/entrar");
		return mv;
	}
	
	@RequestMapping("/formulario")
	public ModelAndView retornaForm() {
		
		ModelAndView mv = new ModelAndView("/Formulario");
		mv.addObject("pessoa", new Pessoa());
		return mv;
	}
	
	@RequestMapping("/salvar")
	public ModelAndView salvar(@Validated Pessoa pessoa, BindingResult result) {
		
		ModelAndView mv = new ModelAndView("/Formulario"); 
		
		if(result.hasErrors()) {
			return mv; 
		}
		
		mv.addObject("mensagem", "Pessoa cadastrada com sucesso!"); 
		pessoaService.cadastrar(pessoa);
		
		return mv;
	}
	
	/*@RequestMapping("/painel")
	public ModelAndView clientePainel() {
		ModelAndView mv = new ModelAndView("painel");
		return mv;
	}*/
	
	@RequestMapping("/listarpedidos")
	public ModelAndView liststarPedidos() {
		ModelAndView mv = new ModelAndView("historicoPedidos");
		Object auth = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		UserDetails user = (UserDetails) auth;
		Pessoa pessoa = pessoaService.buscarProEmail(user.getUsername());
		List<Pedido> pedidos = pedidoService.listarPedidosPorId(pessoa.getCodigo());
		mv.addObject("historicoPedidos", pedidos);
		return mv;
	}
	
	@RequestMapping("/painel/atualizar")
	public ModelAndView atualizarCliente() {
		Object auth = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		UserDetails user = (UserDetails) auth;
		Pessoa pessoa = pessoaService.buscarProEmail(user.getUsername());
		ModelAndView mv = new ModelAndView("cadastroPessoa");
		mv.addObject("pessoa", pessoa);
		return mv;
	}
	
}







