package com.projeto.karteria.controller;

import com.projeto.karteria.model.Usuario;
import com.projeto.karteria.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class PasswordResetController {

  @Autowired private UsuarioService usuarioService;

  @GetMapping("/esqueci-senha")
  public String showForgotPasswordForm() {
    return "esqueci-senha";
  }

  @PostMapping("/esqueci-senha")
  public String processForgotPassword(@RequestParam("email") String email, RedirectAttributes redirectAttributes) {
    // Agora o Service faz tudo sozinho: gera o token e manda o e-mail
    usuarioService.createPasswordResetTokenForUser(email);

    redirectAttributes.addFlashAttribute(
        "mensagem", "Se o e-mail estiver cadastrado, um link de redefinição foi enviado.");
    return "redirect:/esqueci-senha";
  }

  @GetMapping("/resetar-senha")
  public String showResetPasswordForm(@RequestParam("token") String token, Model model) {
    Usuario usuario = usuarioService.validatePasswordResetToken(token);
    
    if (usuario == null) {
      model.addAttribute("erro", "O link é inválido ou já expirou (você tinha 15 minutos). Peça um novo link.");
      return "esqueci-senha";
    }
    
    model.addAttribute("token", token);
    return "resetar-senha";
  }

  @PostMapping("/resetar-senha")
  public String processResetPassword(
      @RequestParam("token") String token,
      @RequestParam("novaSenha") String novaSenha,
      @RequestParam("confirmarSenha") String confirmarSenha,
      RedirectAttributes redirectAttributes) {

    Usuario usuario = usuarioService.validatePasswordResetToken(token);
    
    if (usuario == null) {
      redirectAttributes.addFlashAttribute("erro", "O link é inválido ou expirou.");
      return "redirect:/esqueci-senha";
    }
    
    if (!novaSenha.equals(confirmarSenha)) {
      redirectAttributes.addFlashAttribute("erro", "As senhas não coincidem.");
      return "redirect:/resetar-senha?token=" + token;
    }

    usuarioService.changeUserPassword(usuario, novaSenha);
    
    // Sucesso! Manda pro login.
    redirectAttributes.addFlashAttribute("registrationSuccess", "Senha alterada com sucesso! Faça o login.");
    return "redirect:/login";
  }
}