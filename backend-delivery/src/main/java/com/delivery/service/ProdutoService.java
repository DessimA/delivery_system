package com.delivery.service;

import com.delivery.dto.ProdutoRequestDTO;
import com.delivery.dto.ProdutoResponseDTO;
import com.delivery.mapper.ProdutoMapper;
import com.delivery.model.Produto;
import com.delivery.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private ProdutoMapper produtoMapper;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public List<ProdutoResponseDTO> listarTodos() {
        return produtoRepository.findAll().stream()
                .map(produtoMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public ProdutoResponseDTO buscarPorId(Long id) {
        return produtoRepository.findById(id)
                .map(produtoMapper::toResponseDTO)
                .orElse(null);
    }

    public ProdutoResponseDTO criarProduto(ProdutoRequestDTO produtoDTO, MultipartFile imagem) {
        Produto produto = produtoMapper.toEntity(produtoDTO);
        if (imagem != null && !imagem.isEmpty()) {
            String nomeArquivo = salvarImagem(imagem);
            produto.setCaminhoImagem(nomeArquivo);
        }
        Produto produtoSalvo = produtoRepository.save(produto);
        return produtoMapper.toResponseDTO(produtoSalvo);
    }

    public ProdutoResponseDTO atualizarProduto(Long id, ProdutoRequestDTO produtoDTO) {
        return produtoRepository.findById(id).map(produtoExistente -> {
            produtoExistente.setNomeProduto(produtoDTO.getNomeProduto());
            produtoExistente.setDescricao(produtoDTO.getDescricao());
            produtoExistente.setPreco(produtoDTO.getPreco());
            Produto produtoAtualizado = produtoRepository.save(produtoExistente);
            return produtoMapper.toResponseDTO(produtoAtualizado);
        }).orElse(null);
    }

    public void excluir(Long id) {
        produtoRepository.deleteById(id);
    }

    private String salvarImagem(MultipartFile imagem) {
        try {
            Path diretorioUpload = Paths.get(uploadDir);
            if (!Files.exists(diretorioUpload)) {
                Files.createDirectories(diretorioUpload);
            }

            String nomeArquivoOriginal = imagem.getOriginalFilename();
            String extensao = nomeArquivoOriginal.substring(nomeArquivoOriginal.lastIndexOf("."));
            String nomeArquivoUnico = UUID.randomUUID().toString() + extensao;

            Path caminhoArquivo = diretorioUpload.resolve(nomeArquivoUnico);
            Files.write(caminhoArquivo, imagem.getBytes());

            return nomeArquivoUnico;
        } catch (IOException e) {
            throw new RuntimeException("Falha ao salvar a imagem.", e);
        }
    }
}
