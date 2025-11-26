package com.projeto.karteria.service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.UUID; 

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

  @Autowired private UsuarioRepository usuarioRepository;

  @Autowired private PasswordEncoder passwordEncoder;

  // Gerador seguro
  private static final SecureRandom random = new SecureRandom();
  private static final String NUMEROS = "0123456789";
  private static final String LETRAS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

  // 2. Método para gerar o código: 7 números + 2 letras (Ex: 8492014XB)
  private String gerarCodigoValidacao() {
      StringBuilder sb = new StringBuilder(9);
      for (int i = 0; i < 7; i++) {
          sb.append(NUMEROS.charAt(random.nextInt(NUMEROS.length())));
      }
      for (int i = 0; i < 2; i++) {
          sb.append(LETRAS.charAt(random.nextInt(LETRAS.length())));
      }
      return sb.toString();
  }

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    return usuarioRepository
        .findByEmail(email)
        .orElseThrow(
            () -> new UsernameNotFoundException("Usuário não encontrado com o e-mail: " + email));
  }

  // --- Registro de usuário ---
  public void registerUserBasic(Usuario usuario) {
    if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
      throw new IllegalStateException("E-mail já cadastrado.");
    }
    usuario.setSenha(passwordEncoder.encode(usuario.getPassword())); 
    usuario.setTipo(null); 
    usuario.setCadastroCompleto(false); 
    
    // 3. Gera e salva o código no registro
    usuario.setCodigoValidacao(gerarCodigoValidacao());
    
    // Salva a data de cadastro se não existir
    if(usuario.getDataCadastro() == null) {
        usuario.setDataCadastro(LocalDateTime.now());
    }

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
    usuario.setResetTokenExpiry(LocalDateTime.now().plusHours(1));

    usuarioRepository.save(usuario);
    return token;
  }

  public Usuario validatePasswordResetToken(String token) {
    Usuario usuario = usuarioRepository.findByResetToken(token).orElse(null);
    if (usuario == null || usuario.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
      return null;
    }
    return usuario;
  }

  public void changeUserPassword(Usuario usuario, String newPassword) {
    usuario.setSenha(passwordEncoder.encode(newPassword));
    usuario.setResetToken(null); 
    usuario.setResetTokenExpiry(null);
    usuarioRepository.save(usuario);
  }
}