package com.delivery.service;

import com.delivery.model.Estabelecimento;
import com.delivery.model.Usuario;
import com.delivery.repository.EstabelecimentoRepository;
import com.delivery.repository.UsuarioRepository;
import com.delivery.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EstabelecimentoService {

    @Autowired
    private EstabelecimentoRepository estabelecimentoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<Estabelecimento> listarTodos() {
        return estabelecimentoRepository.findAll();
    }

    public Optional<Estabelecimento> buscarPorId(Long id) {
        return estabelecimentoRepository.findById(id);
    }

    public Estabelecimento salvar(Estabelecimento estabelecimento) {
        return estabelecimentoRepository.save(estabelecimento);
    }

    public void deletar(Long id) {
        estabelecimentoRepository.deleteById(id);
    }

    public Estabelecimento buscarMeuEstabelecimento() {
        Usuario usuario = getAuthenticatedUser();
        Estabelecimento estabelecimento = usuario.getEstabelecimento();
        if (estabelecimento == null) {
            throw new IllegalStateException("Usuário não tem um estabelecimento associado.");
        }
        return estabelecimento;
    }

    private Usuario getAuthenticatedUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioRepository.findByEmail(email);
        if (usuario == null) {
            throw new UserNotFoundException("Inconsistência de dados: usuário autenticado não encontrado: " + email);
        }
        return usuario;
    }
}
