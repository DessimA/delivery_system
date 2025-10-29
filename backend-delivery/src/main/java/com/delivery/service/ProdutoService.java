package com.delivery.service;

import com.delivery.dto.ProdutoRequestDTO;
import com.delivery.dto.ProdutoResponseDTO;
import com.delivery.mapper.ProdutoMapper;
import com.delivery.model.Estabelecimento;
import com.delivery.model.Usuario;
import com.delivery.model.Produto;
import com.delivery.repository.UsuarioRepository;
import com.delivery.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.delivery.exception.UserNotFoundException;
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
    private final UsuarioRepository usuarioRepository;
    private final ProdutoMapper produtoMapper;
    private final String uploadDir;

    public ProdutoService(ProdutoRepository produtoRepository, UsuarioRepository usuarioRepository, ProdutoMapper produtoMapper, @Value("${file.upload-dir}") String uploadDir) {
        this.produtoRepository = produtoRepository;
        this.usuarioRepository = usuarioRepository;
        this.produtoMapper = produtoMapper;
        this.uploadDir = uploadDir;
    }

    public List<ProdutoResponseDTO> listarTodos() {
        return produtoRepository.findAll().stream()
                .map(produtoMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public List<ProdutoResponseDTO> listarDoMeuEstabelecimento() {
        Usuario usuario = getAuthenticatedUser();
        Estabelecimento estabelecimento = usuario.getEstabelecimento();
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

        Usuario usuarioLogado = getAuthenticatedUser();
        Estabelecimento estabelecimento = usuarioLogado.getEstabelecimento();
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
        Usuario usuarioLogado = getAuthenticatedUser();
        Produto produtoExistente = produtoRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Produto não encontrado com o ID: " + id));

        // TODO: Substituir por anotações de segurança. Ex: @PreAuthorize("@customSecurity.podeModificarProduto(authentication, #id)")
        if (!isUsuarioAutorizadoParaModificar(produtoExistente, usuarioLogado)) {
            throw new SecurityException("Usuário não autorizado a modificar este produto.");
        }

        produtoExistente.setNomeProduto(produtoRequestDTO.getNomeProduto());
        produtoExistente.setDescricao(produtoRequestDTO.getDescricao());
        produtoExistente.setPreco(produtoRequestDTO.getPreco());
        Produto produtoAtualizado = produtoRepository.save(produtoExistente);
        return produtoMapper.toResponseDTO(produtoAtualizado);
    }

    public void excluir(Long id) {
        Usuario usuarioLogado = getAuthenticatedUser();
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Produto não encontrado com o ID: " + id));

        // TODO: Substituir por anotações de segurança.
        if (!isUsuarioAutorizadoParaModificar(produto, usuarioLogado)) {
            throw new SecurityException("Usuário não autorizado a excluir este produto.");
        }
        produtoRepository.delete(produto);
    }

    private Usuario getAuthenticatedUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioRepository.findByEmail(email);
        if (usuario == null) {
            throw new UserNotFoundException("Inconsistência de dados: usuário autenticado não encontrado: " + email);
        }
        return usuario;
    }

    private boolean isUsuarioAutorizadoParaModificar(Produto produto, Usuario usuario) {
        if (usuario.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(ROLE_ADMIN))) {
            return true;
        }
        Estabelecimento estabelecimentoUsuario = usuario.getEstabelecimento();
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
