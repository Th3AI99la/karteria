package com.projeto.karteria.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.projeto.karteria.model.Usuario;
import com.projeto.karteria.service.UsuarioService;

@Controller
public class AuthController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/login")
    public String showLoginForm() {
        return "login"; // Mostra a página login.html
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "register"; // Mostra a página register.html
    }

    @PostMapping("/register")
    public String processRegistration(@ModelAttribute("usuario") Usuario usuario, Model model) {
        try {
            usuarioService.registerUser(usuario);
            model.addAttribute("registrationSuccess", "Registro realizado com sucesso! Faça o login.");
            return "login";
        } catch (IllegalStateException e) {
            model.addAttribute("registrationError", e.getMessage());
            return "register";
        }
    }
}