package com.delivery.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.delivery.model.Produto;
import com.delivery.repository.ProdutoRepository;
import com.delivery.util.AulaFileUtils;

@Service
public class ProdutoService {

	@Autowired
	private ProdutoRepository produtoRepository;
	
	public void cadastrar(Produto produto, MultipartFile imagem) {
		
		if (!imagem.isEmpty()) {
			String caminho = "images/" + produto.getIdProduto() + ".png"; 
			AulaFileUtils.salvarImagem(caminho,imagem);
		}
		
		produtoRepository.save(produto);
	}

	public List<Produto> listarPedidos() {
		return produtoRepository.findAll();
	}

	public void excluir(Long idProduto) {
		produtoRepository.deleteById(idProduto);
		
	}

	public Produto atualizar(Long idProduto) {
		return produtoRepository.getOne(idProduto);
	}
	
	public Produto buscarPorId(Long id){
		return produtoRepository.getOne(id);
	}
	
}
