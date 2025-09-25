package com.projeto.karteria.service;

import com.projeto.karteria.model.Usuario;
import com.projeto.karteria.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    // CREATE
    public Usuario criarUsuario(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    // READ (All)
    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    // READ (by Id)
    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    // UPDATE
    public Usuario atualizarUsuario(Long id, Usuario usuarioDetails) {
        Usuario usuario = usuarioRepository.findById(id).orElseThrow(() -> new RuntimeException("Usuário não encontrado com o id: " + id));
        
        usuario.setNome(usuarioDetails.getNome());
        usuario.setEmail(usuarioDetails.getEmail());
        usuario.setSenha(usuarioDetails.getSenha());
        usuario.setTelefone(usuarioDetails.getTelefone());
        
        return usuarioRepository.save(usuario);
    }

    // DELETE
    public void deletarUsuario(Long id) {
        usuarioRepository.deleteById(id);
    }
}