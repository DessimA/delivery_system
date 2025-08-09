package com.delivery.service;

import com.delivery.dto.ProdutoRequestDTO;
import com.delivery.dto.ProdutoResponseDTO;
import com.delivery.mapper.ProdutoMapper;
import com.delivery.model.Estabelecimento;
import com.delivery.model.Pessoa;
import com.delivery.model.Produto;
import com.delivery.repository.PessoaRepository;
import com.delivery.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
    private PessoaRepository pessoaRepository; // Adicionado para buscar o usuário

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

        // Associa o produto ao estabelecimento do usuário logado
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userEmail;
        if (principal instanceof UserDetails) {
            userEmail = ((UserDetails)principal).getUsername();
        } else {
            userEmail = principal.toString();
        }

        Pessoa pessoa = pessoaRepository.findByEmail(userEmail);
        if (pessoa != null && pessoa.getEstabelecimento() != null) {
            produto.setEstabelecimento(pessoa.getEstabelecimento());
        } else {
            // Lançar exceção ou tratar o caso de usuário sem estabelecimento
            throw new IllegalStateException("Usuário não tem um estabelecimento associado ou não foi encontrado.");
        }

        if (imagem != null && !imagem.isEmpty()) {
            String nomeArquivo = salvarImagem(imagem);
            produto.setCaminhoImagem(nomeArquivo);
        }
        Produto produtoSalvo = produtoRepository.save(produto);
        return produtoMapper.toResponseDTO(produtoSalvo);
    }

    public ProdutoResponseDTO atualizarProduto(Long id, ProdutoRequestDTO produtoDTO) {
        Pessoa pessoa = getAuthenticatedUser();
        return produtoRepository.findById(id).map(produtoExistente -> {
            if (!produtoPertenceAoUsuario(produtoExistente, pessoa)) {
                throw new SecurityException("Usuário não autorizado a modificar este produto.");
            }
            produtoExistente.setNomeProduto(produtoDTO.getNomeProduto());
            produtoExistente.setDescricao(produtoDTO.getDescricao());
            produtoExistente.setPreco(produtoDTO.getPreco());
            Produto produtoAtualizado = produtoRepository.save(produtoExistente);
            return produtoMapper.toResponseDTO(produtoAtualizado);
        }).orElse(null);
    }

    public void excluir(Long id) {
        Pessoa pessoa = getAuthenticatedUser();
        produtoRepository.findById(id).ifPresent(produto -> {
            if (!produtoPertenceAoUsuario(produto, pessoa)) {
                throw new SecurityException("Usuário não autorizado a excluir este produto.");
            }
            produtoRepository.delete(produto);
        });
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

    private boolean produtoPertenceAoUsuario(Produto produto, Pessoa pessoa) {
        // ADMIN pode modificar qualquer produto
        if (pessoa.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return true;
        }
        // Usuário RESTAURANT só pode modificar produtos do seu estabelecimento
        if (pessoa.getEstabelecimento() != null && produto.getEstabelecimento() != null) {
            return produto.getEstabelecimento().getId().equals(pessoa.getEstabelecimento().getId());
        }
        return false;
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
