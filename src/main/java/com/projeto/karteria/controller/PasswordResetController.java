package com.projeto.karteria.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.projeto.karteria.model.Usuario;
import com.projeto.karteria.service.UsuarioService;

@Controller
public class PasswordResetController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/esqueci-senha")
    public String showForgotPasswordForm() {
        return "esqueci-senha";
    }

    @PostMapping("/esqueci-senha")
    public String processForgotPassword(@RequestParam("email") String email, RedirectAttributes redirectAttributes) {
        try {
            String token = usuarioService.createPasswordResetTokenForUser(email);
            // SIMULAÇÃO DE ENVIO DE E-MAIL: Imprimimos o link no console
            String resetUrl = "http://localhost:8080/resetar-senha?token=" + token;
            System.out.println("==================================================");
            System.out.println("Link para resetar a senha: " + resetUrl);
            System.out.println("==================================================");

        } catch (Exception e) {
            // Se o e-mail não existir, não informamos por segurança, mas o processo continua
        }
        
        redirectAttributes.addFlashAttribute("mensagem", "Se o e-mail estiver cadastrado, um link de redefinição foi enviado.");
        return "redirect:/esqueci-senha";
    }

    @GetMapping("/resetar-senha")
    public String showResetPasswordForm(@RequestParam("token") String token, Model model) {
        Usuario usuario = usuarioService.validatePasswordResetToken(token);
        if (usuario == null) {
            model.addAttribute("erro", "Token inválido ou expirado.");
            return "esqueci-senha"; // Ou uma página de erro específica
        }
        model.addAttribute("token", token);
        return "resetar-senha";
    }

    @PostMapping("/resetar-senha")
    public String processResetPassword(@RequestParam("token") String token,
                                       @RequestParam("novaSenha") String novaSenha,
                                       @RequestParam("confirmarSenha") String confirmarSenha,
                                       RedirectAttributes redirectAttributes) {

        Usuario usuario = usuarioService.validatePasswordResetToken(token);
        if (usuario == null) {
            redirectAttributes.addFlashAttribute("erro", "Token inválido ou expirado.");
            return "redirect:/esqueci-senha";
        }
        if (!novaSenha.equals(confirmarSenha)) {
            redirectAttributes.addFlashAttribute("erro", "As senhas não coincidem.");
            return "redirect:/resetar-senha?token=" + token;
        }

        usuarioService.changeUserPassword(usuario, novaSenha);
        redirectAttributes.addFlashAttribute("registrationSuccess", "Senha alterada com sucesso! Faça o login.");
        return "redirect:/login";
    }
}