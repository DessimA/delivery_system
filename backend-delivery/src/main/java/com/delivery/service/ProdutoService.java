package com.delivery.service;

import com.delivery.dto.ProdutoRequestDTO;
import com.delivery.dto.ProdutoResponseDTO;
import com.delivery.mapper.ProdutoMapper;
import com.delivery.model.Estabelecimento;
import com.delivery.model.Pessoa;
import com.delivery.model.Produto;
import com.delivery.repository.PessoaRepository;
import com.delivery.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
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

    private static final String ROLE_ADMIN = "ROLE_ADMIN";

    private final ProdutoRepository produtoRepository;
    private final PessoaRepository pessoaRepository;
    private final ProdutoMapper produtoMapper;
    private final String uploadDir;

    public ProdutoService(ProdutoRepository produtoRepository, PessoaRepository pessoaRepository, ProdutoMapper produtoMapper, @Value("${file.upload-dir}") String uploadDir) {
        this.produtoRepository = produtoRepository;
        this.pessoaRepository = pessoaRepository;
        this.produtoMapper = produtoMapper;
        this.uploadDir = uploadDir;
    }

    public List<ProdutoResponseDTO> listarTodos() {
        return produtoRepository.findAll().stream()
                .map(produtoMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public List<ProdutoResponseDTO> listarDoMeuEstabelecimento() {
        Pessoa pessoa = getAuthenticatedUser();
        Estabelecimento estabelecimento = pessoa.getEstabelecimento();
        if (estabelecimento == null) {
            throw new IllegalStateException("Usuário não é dono de um estabelecimento.");
        }
        return produtoRepository.findByEstabelecimentoId(estabelecimento.getId()).stream()
                .map(produtoMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public ProdutoResponseDTO buscarPorId(Long id) {
        return produtoRepository.findById(id)
                .map(produtoMapper::toResponseDTO)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado com o ID: " + id));
    }

    public ProdutoResponseDTO criarProduto(ProdutoRequestDTO produtoRequestDTO, MultipartFile imagem) {
        Produto produto = produtoMapper.toEntity(produtoRequestDTO);

        Pessoa pessoaLogada = getAuthenticatedUser();
        Estabelecimento estabelecimento = pessoaLogada.getEstabelecimento();
        if (estabelecimento == null) {
            throw new IllegalStateException("Usuário não tem um estabelecimento associado para criar produtos.");
        }
        produto.setEstabelecimento(estabelecimento);

        if (imagem != null && !imagem.isEmpty()) {
            String nomeArquivo = salvarImagem(imagem);
            produto.setCaminhoImagem(nomeArquivo);
        }
        Produto produtoSalvo = produtoRepository.save(produto);
        return produtoMapper.toResponseDTO(produtoSalvo);
    }

    public ProdutoResponseDTO atualizarProduto(Long id, ProdutoRequestDTO produtoRequestDTO) {
        Pessoa pessoaLogada = getAuthenticatedUser();
        Produto produtoExistente = produtoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado com o ID: " + id));

        // TODO: Substituir por anotações de segurança. Ex: @PreAuthorize("@customSecurity.podeModificarProduto(authentication, #id)")
        if (!isUsuarioAutorizadoParaModificar(produtoExistente, pessoaLogada)) {
            throw new SecurityException("Usuário não autorizado a modificar este produto.");
        }

        produtoExistente.setNomeProduto(produtoRequestDTO.getNomeProduto());
        produtoExistente.setDescricao(produtoRequestDTO.getDescricao());
        produtoExistente.setPreco(produtoRequestDTO.getPreco());
        Produto produtoAtualizado = produtoRepository.save(produtoExistente);
        return produtoMapper.toResponseDTO(produtoAtualizado);
    }

    public void excluir(Long id) {
        Pessoa pessoaLogada = getAuthenticatedUser();
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado com o ID: " + id));

        // TODO: Substituir por anotações de segurança.
        if (!isUsuarioAutorizadoParaModificar(produto, pessoaLogada)) {
            throw new SecurityException("Usuário não autorizado a excluir este produto.");
        }
        produtoRepository.delete(produto);
    }

    private Pessoa getAuthenticatedUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Pessoa pessoa = pessoaRepository.findByEmail(email);
        if (pessoa == null) {
            throw new IllegalStateException("Inconsistência de dados: usuário autenticado não encontrado: " + email);
        }
        return pessoa;
    }

    private boolean isUsuarioAutorizadoParaModificar(Produto produto, Pessoa pessoa) {
        if (pessoa.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(ROLE_ADMIN))) {
            return true;
        }
        Estabelecimento estabelecimentoUsuario = pessoa.getEstabelecimento();
        Estabelecimento estabelecimentoProduto = produto.getEstabelecimento();
        return estabelecimentoUsuario != null && estabelecimentoProduto != null &&
                estabelecimentoProduto.getId().equals(estabelecimentoUsuario.getId());
    }

    // TODO: Extrair a lógica de armazenamento de arquivos para um FileStorageService dedicado.
    private String salvarImagem(MultipartFile imagem) {
        try {
            Path diretorioUpload = Paths.get(uploadDir);
            if (!Files.exists(diretorioUpload)) {
                Files.createDirectories(diretorioUpload);
            }

            String nomeArquivoOriginal = imagem.getOriginalFilename();
            String extensao = "";
            if (nomeArquivoOriginal != null && nomeArquivoOriginal.contains(".")) {
                extensao = nomeArquivoOriginal.substring(nomeArquivoOriginal.lastIndexOf("."));
            }
            String nomeArquivoUnico = UUID.randomUUID().toString() + extensao;

            Path caminhoArquivo = diretorioUpload.resolve(nomeArquivoUnico);
            Files.write(caminhoArquivo, imagem.getBytes());

            return nomeArquivoUnico;
        } catch (IOException e) {
            throw new RuntimeException("Falha ao salvar a imagem.", e);
        }
    }
}
