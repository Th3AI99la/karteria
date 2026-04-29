package com.projeto.karteria.service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.projeto.karteria.model.Usuario;
import com.projeto.karteria.model.PasswordResetToken;
import com.projeto.karteria.repository.UsuarioRepository;
import com.projeto.karteria.repository.PasswordResetTokenRepository;

import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.SimpleMailMessage;

@Service
public class UsuarioService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // AUTOWIRED DO TOKEN REPOSITORY E JAVA MAIL SENDER PARA RECUPERAÇÃO DE SENHA
    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Value("${app.url}")
    private String appUrl;

    // Gerador seguro
    private static final SecureRandom random = new SecureRandom();
    private static final String NUMEROS = "0123456789";
    private static final String LETRAS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

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

        usuario.setCodigoValidacao(gerarCodigoValidacao());

        if (usuario.getDataCadastro() == null) {
            usuario.setDataCadastro(LocalDateTime.now());
        }

        usuarioRepository.save(usuario);
    }

    // =========================================================
    // TOKEN ENTITY
    // =========================================================

    @Transactional
    public void createPasswordResetTokenForUser(String email) {
        // 1. Acha o usuário pelo E-mail
        Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);
        if (usuario == null) {
            return; // Se não achar, para silenciosamente por segurança
        }

        // 2. Gera a nova string do Token
        String newTokenString = UUID.randomUUID().toString();

        // 3. Pega o token existente no banco OU cria um novo se não existir
        PasswordResetToken myToken = tokenRepository.findByUsuario(usuario)
                .orElse(new PasswordResetToken()); // Cria um novo objeto vazio se não achar

        // 4. Atualiza os dados (isso resolve o erro de chave duplicada!)
        myToken.setToken(newTokenString);
        myToken.setUsuario(usuario);
        myToken.setDataExpiracao(LocalDateTime.now().plusMinutes(15));

        tokenRepository.save(myToken);

        // 5. Monta o link mágico e envia
        String resetLink = appUrl + "/resetar-senha?token=" + newTokenString;
        enviarEmailDeRecuperacao(usuario.getEmail(), resetLink);
    }

    private void enviarEmailDeRecuperacao(String emailPara, String link) {
        try {
            // Cria uma mensagem MIME (suporta HTML e anexos)
            MimeMessage mensagem = mailSender.createMimeMessage();

            // O 'true' indica que a mensagem será multipart (HTML)
            MimeMessageHelper helper = new MimeMessageHelper(mensagem, true, "UTF-8");

            helper.setTo(emailPara);
            helper.setSubject("Karteria - Redefinição de Senha");

            // Template HTML do e-mail (usando Text Blocks do Java)
            String htmlMsg = """
                    <table width="100%%" cellpadding="0" cellspacing="0" border="0" style="background-color: #f8f9fa; padding: 40px 0; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;">
                        <tr>
                            <td align="center">
                                <table width="600" cellpadding="0" cellspacing="0" border="0" style="background-color: #ffffff; border-radius: 8px; border: 1px solid #e2e8f0; overflow: hidden; box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);">

                                    <!-- Faixa de Destaque no Topo (Substitua pela cor primária do Karteria) -->
                                    <tr>
                                        <td style="background-color: #198754; height: 6px;"></td> <!-- SEU_HEX_AQUI -->
                                    </tr>

                                    <!-- Logo (Largura aumentada para compensar o formato 3456x1246) -->
                                    <tr>
                                        <td align="center" style="padding: 40px 0 20px 0;">
                                            <img src="https://github.com/Th3AI99la/karteria/blob/main/src/main/resources/static/images/logo-mail.png?raw=true" alt="Karteria" width="280" style="display: block; max-width: 100%%; height: auto;">
                                        </td>
                                    </tr>

                                    <!-- Título -->
                                    <tr>
                                        <td align="center" style="color: #1e293b; font-size: 24px; font-weight: 700; padding-bottom: 15px;">
                                            Recuperação de Senha
                                        </td>
                                    </tr>

                                    <!-- Saudação -->
                                    <tr>
                                        <td style="color: #475569; font-size: 16px; padding: 0 40px 10px 40px;">
                                            Olá,
                                        </td>
                                    </tr>

                                    <!-- Texto -->
                                    <tr>
                                        <td style="color: #475569; font-size: 16px; line-height: 1.6; padding: 0 40px;">
                                            Recebemos uma solicitação para redefinir a senha da sua conta no <strong>Karteria</strong>.
                                            Se foi você, clique no botão abaixo para criar uma nova senha com segurança.
                                        </td>
                                    </tr>

                                    <!-- Botão -->
                                    <tr>
                                        <td align="center" style="padding: 35px 0;">
                                            <!-- Substitua o background-color pela cor primária do Karteria -->
                                            <a href="%s" style="background-color: #198754; color: #ffffff; padding: 14px 32px; text-decoration: none; border-radius: 6px; font-weight: bold; display: inline-block; font-size: 16px; transition: background-color 0.3s;">
                                                Redefinir minha senha
                                            </a>
                                        </td>
                                    </tr>

                                    <!-- Aviso -->
                                    <tr>
                                        <td align="center" style="color: #64748b; font-size: 14px; padding-bottom: 20px;">
                                            ⏱️ Este link é válido por <strong>15 minutos</strong>.
                                        </td>
                                    </tr>

                                    <!-- Linha Divisória -->
                                    <tr>
                                        <td style="padding: 0 40px;">
                                            <hr style="border: none; border-top: 1px solid #e2e8f0;">
                                        </td>
                                    </tr>

                                    <!-- Rodapé -->
                                    <tr>
                                        <td align="center" style="color: #94a3b8; font-size: 12px; line-height: 1.6; padding: 20px 40px 30px 40px;">
                                            Se você não solicitou esta alteração, ignore este e-mail.<br>
                                            Sua senha permanecerá inalterada.<br><br>
                                            <strong>Equipe Karteria &copy; 2026</strong>
                                        </td>
                                    </tr>

                                </table>
                            </td>
                        </tr>
                    </table>
                    """
                    .formatted(link);

            // O segundo parâmetro 'true' avisa ao Java que o conteúdo é HTML
            helper.setText(htmlMsg, true);

            mailSender.send(mensagem);
            System.out.println("E-mail HTML enviado com sucesso para: " + emailPara);

        } catch (Exception e) {
            System.err.println("Erro ao enviar e-mail HTML: " + e.getMessage());
        }
    }

    public Usuario validatePasswordResetToken(String token) {
        PasswordResetToken passToken = tokenRepository.findByToken(token).orElse(null);

        if (passToken == null)
            return null;

        if (passToken.getDataExpiracao().isBefore(LocalDateTime.now())) {
            tokenRepository.delete(passToken);
            return null;
        }

        return passToken.getUsuario();
    }

    @Transactional
    public void changeUserPassword(Usuario usuario, String novaSenha) {
        usuario.setSenha(passwordEncoder.encode(novaSenha));
        usuarioRepository.save(usuario);
        tokenRepository.deleteByUsuario(usuario);
    }
}