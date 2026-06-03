package com.projeto.karteria.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.projeto.karteria.model.Usuario;
import com.projeto.karteria.repository.UsuarioRepository;

@Controller
public class SegurancaController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/seguranca")
    public String showSegurancaPage(Model model, Authentication authentication) {
        String email = authentication.getName();
        Usuario usuario = usuarioRepository.findByEmail(email).orElseThrow();

        model.addAttribute("usuario", usuario);
        return "seguranca";
    }

    @PostMapping("/seguranca/alterar-senha")
    public String alterarSenha(
            @RequestParam String senhaAtual,
            @RequestParam String novaSenha,
            @RequestParam String confirmarSenha,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        Usuario usuario = usuarioRepository.findByEmail(authentication.getName()).orElseThrow();

        // 1. Verifica senha atual
        if (!passwordEncoder.matches(senhaAtual, usuario.getSenha())) {
            redirectAttributes.addFlashAttribute("erroSenha", "A senha atual está incorreta.");
            return "redirect:/seguranca";
        }

        // 2. Verifica confirmação
        if (!novaSenha.equals(confirmarSenha)) {
            redirectAttributes.addFlashAttribute("erroSenha", "A nova senha e a confirmação não coincidem.");
            return "redirect:/seguranca";
        }

        if (novaSenha.length() < 6) {
            redirectAttributes.addFlashAttribute("erroSenha", "A nova senha deve ter pelo menos 6 caracteres.");
            return "redirect:/seguranca";
        }

        // 3. Salva nova senha
        usuario.setSenha(passwordEncoder.encode(novaSenha));
        usuarioRepository.save(usuario);

        redirectAttributes.addFlashAttribute("sucesso", "Senha alterada com sucesso!");
        return "redirect:/seguranca";
    }

    @PostMapping("/seguranca/atualizar-dados")
    public String atualizarDados(
            @RequestParam String email,
            @RequestParam String telefone,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        // Nota: Alterar e-mail geralmente requer re-verificação em apps reais.
        // Aqui faremos a alteração direta para simplificar.

        Usuario usuario = usuarioRepository.findByEmail(authentication.getName()).orElseThrow();

        String emailNormalizado = email != null ? email.trim() : "";
        String telefoneNormalizado = telefone != null ? telefone.trim() : "";

        if (emailNormalizado.isEmpty() || telefoneNormalizado.isEmpty()) {
            redirectAttributes.addFlashAttribute("erroDados", "E-mail e telefone são obrigatórios.");
            return "redirect:/seguranca";
        }

        // Verifica se o novo email já existe (se for diferente do atual)
        if (!usuario.getEmail().equals(emailNormalizado) && usuarioRepository.findByEmail(emailNormalizado).isPresent()) {
            redirectAttributes.addFlashAttribute("erroDados", "Este e-mail já está em uso por outro usuário.");
            return "redirect:/seguranca";
        }

        usuario.setEmail(emailNormalizado);
        usuario.setTelefone(telefoneNormalizado);
        usuarioRepository.save(usuario);

        UsernamePasswordAuthenticationToken novaAutenticacao = new UsernamePasswordAuthenticationToken(
                usuario,
                authentication.getCredentials(),
                usuario.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(novaAutenticacao);

        redirectAttributes.addFlashAttribute("sucessoDados", "Dados de segurança atualizados.");
        return "redirect:/seguranca";
    }
}
