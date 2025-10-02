package com.projeto.karteria.service;

import java.time.LocalDateTime; // Adicionado
import java.util.UUID; // Adicionado

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.projeto.karteria.model.Usuario;
import com.projeto.karteria.repository.UsuarioRepository;

@Service
public class UsuarioService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com o e-mail: " + email));
    }

    public void registerUser(Usuario usuario) {
        if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            throw new IllegalStateException("E-mail já cadastrado.");
        }
        usuario.setSenha(passwordEncoder.encode(usuario.getPassword()));
        usuarioRepository.save(usuario);
    }

    // --- Reset de senha ---
    public String createPasswordResetTokenForUser(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Email não encontrado"));
        
        String token = UUID.randomUUID().toString();
        usuario.setResetToken(token);
        // Token expira em 1 hora
        usuario.setResetTokenExpiry(LocalDateTime.now().plusHours(1));
        
        usuarioRepository.save(usuario);
        return token;
    }

    public Usuario validatePasswordResetToken(String token) {
        Usuario usuario = usuarioRepository.findByResetToken(token)
                .orElse(null);

        if (usuario == null || usuario.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            return null; // Token inválido ou expirado
        }
        return usuario;
    }

    public void changeUserPassword(Usuario usuario, String newPassword) {
        usuario.setSenha(passwordEncoder.encode(newPassword));
        usuario.setResetToken(null); // Limpa o token após o uso
        usuario.setResetTokenExpiry(null);
        usuarioRepository.save(usuario);
    }
}
