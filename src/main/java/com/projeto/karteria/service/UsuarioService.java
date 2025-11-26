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

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Gerador seguro
    private static final SecureRandom random = new SecureRandom();
    private static final String NUMEROS = "0123456789";
    private static final String LETRAS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    // Método para gerar código 7 números + 2 letras (ex: 1234567AB)
    private String gerarCodigoValidacao() {
        StringBuilder sb = new StringBuilder(9);

        // 7 números
        for (int i = 0; i < 7; i++) {
            sb.append(NUMEROS.charAt(random.nextInt(NUMEROS.length())));
        }

        // 2 letras maiúsculas
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
        // Verifica se email já existe
        if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            throw new IllegalStateException("E-mail já cadastrado.");
        }

        // Define campos iniciais
        usuario.setSenha(passwordEncoder.encode(usuario.getPassword()));
        usuario.setTipo(null);
        usuario.setCadastroCompleto(false);
        usuario.setDataCadastro(LocalDateTime.now());

        // === GERA O CÓDIGO DE VALIDAÇÃO AQUI ===
        usuario.setCodigoValidacao(gerarCodigoValidacao());

        // Salva o usuário no banco de dados
        usuarioRepository.save(usuario);
    }

    // --- Reset de senha ---
    public String createPasswordResetTokenForUser(String email) {
        Usuario usuario = usuarioRepository
                .findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Email não encontrado"));

        String token = UUID.randomUUID().toString();
        usuario.setResetToken(token);
        usuario.setResetTokenExpiry(LocalDateTime.now().plusHours(1));

        usuarioRepository.save(usuario);
        return token;
    }

    // Valida o token de reset
    public Usuario validatePasswordResetToken(String token) {
        Usuario usuario = usuarioRepository.findByResetToken(token).orElse(null);

        if (usuario == null || usuario.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            return null;
        }
        return usuario;
    }

    // Altera a senha
    public void changeUserPassword(Usuario usuario, String newPassword) {
        usuario.setSenha(passwordEncoder.encode(newPassword));
        usuario.setResetToken(null);
        usuario.setResetTokenExpiry(null);
        usuarioRepository.save(usuario);
    }
}

