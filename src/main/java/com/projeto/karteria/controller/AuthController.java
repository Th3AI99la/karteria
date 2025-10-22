package com.projeto.karteria.controller;

import com.projeto.karteria.model.Usuario;
import com.projeto.karteria.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

  // Serviço de Usuário
  @Autowired private UsuarioService usuarioService;

  // MÉTODO PARA MOSTRAR FORMULÁRIO DE LOGIN
  @GetMapping("/login")
  public String showLoginForm() {
    return "login";
  }

  // MÉTODO PARA MOSTRAR FORMULÁRIO DE REGISTRO
  @GetMapping("/register")
  public String showRegistrationForm(Model model) {
    model.addAttribute("usuario", new Usuario());
    return "register";
  }

  // MÉTODO PARA PROCESSAR REGISTRO DE USUÁRIO
  @PostMapping("/register")
  public String processRegistration(
      @ModelAttribute("usuario") Usuario usuario, RedirectAttributes redirectAttributes) {
    try {
      usuarioService.registerUser(usuario);
      redirectAttributes.addFlashAttribute(
          "registrationSuccess", "Registro realizado com sucesso! Faça o login.");
      return "redirect:/login";
    } catch (IllegalStateException e) {
      redirectAttributes.addFlashAttribute("registrationError", e.getMessage());
      return "redirect:/register";
    }
  }
}
