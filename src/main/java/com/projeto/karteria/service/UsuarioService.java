package com.projeto.karteria.service;

import com.projeto.karteria.model.Usuario;
import com.projeto.karteria.repository.UsuarioRepository;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService implements UserDetailsService {

  @Autowired private UsuarioRepository usuarioRepository;

  @Autowired private PasswordEncoder passwordEncoder;

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    return usuarioRepository
        .findByEmail(email)
        .orElseThrow(
            () -> new UsernameNotFoundException("Usuário não encontrado com o e-mail: " + email));
  }

  // --- Registro de usuário ---
  public void registerUserBasic(Usuario usuario) {
    // Verifica se email já existe
    if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
      throw new IllegalStateException("E-mail já cadastrado.");
    }
    // Define campos iniciais
    usuario.setSenha(passwordEncoder.encode(usuario.getPassword())); // Encodar senha
    usuario.setTipo(null); // Tipo será definido depois
    usuario.setCadastroCompleto(false); // Cadastro incompleto

    usuarioRepository.save(usuario);
  }

  // --- Reset de senha ---
  public String createPasswordResetTokenForUser(String email) {
    Usuario usuario =
        usuarioRepository
            .findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("Email não encontrado"));

    String token = UUID.randomUUID().toString();
    usuario.setResetToken(token);
    // Token expira em 1 hora
    usuario.setResetTokenExpiry(LocalDateTime.now().plusHours(1));

    usuarioRepository.save(usuario);
    return token;
  }

  // Valida o token de reset de senha
  public Usuario validatePasswordResetToken(String token) {
    Usuario usuario = usuarioRepository.findByResetToken(token).orElse(null);

    if (usuario == null || usuario.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
      return null; // Token inválido ou expirado
    }
    return usuario;
  }

  // Altera a senha do usuário
  public void changeUserPassword(Usuario usuario, String newPassword) {
    usuario.setSenha(passwordEncoder.encode(newPassword));
    usuario.setResetToken(null); // Limpa o token após o uso
    usuario.setResetTokenExpiry(null);
    usuarioRepository.save(usuario);
  }
}
