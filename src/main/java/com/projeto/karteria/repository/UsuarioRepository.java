package com.projeto.karteria.repository;

import java.util.ArrayList; // <-- Adicionar este import
import java.util.List;      // <-- Adicionar este import
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Repository;

import com.projeto.karteria.model.Usuario;

@Repository
public class UsuarioRepository {
    private final Map<String, Usuario> usuariosByEmail = new ConcurrentHashMap<>();
    private final AtomicLong idCounter = new AtomicLong();

    public Usuario save(Usuario usuario) {
        if (usuario.getId() == null) {
            usuario.setId(idCounter.incrementAndGet());
        }
        usuariosByEmail.put(usuario.getEmail(), usuario);
        return usuario;
    }

    public Optional<Usuario> findByEmail(String email) {
        return Optional.ofNullable(usuariosByEmail.get(email));
    }

    public List<Usuario> findAll() {
        return new ArrayList<>(usuariosByEmail.values());
    }
}