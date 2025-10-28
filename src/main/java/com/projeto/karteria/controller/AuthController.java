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

import jakarta.servlet.http.HttpSession; 

@Controller
public class AuthController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        // Não precisa mais do objeto Usuario aqui inicialmente
        // model.addAttribute("usuario", new Usuario());
        return "register";
    }

    // ** MÉTODO MODIFICADO **
    @PostMapping("/register")
    public String processRegistration(
            @RequestParam String email, // Recebe parâmetros
            @RequestParam String senha,
            @RequestParam String confirmarSenha,
            HttpSession session, // Injeta HttpSession
            RedirectAttributes redirectAttributes) {

        // 1. Validação básica (poderia ser mais robusta)
        if (email == null || email.isEmpty() || senha == null || senha.isEmpty()) {
            redirectAttributes.addFlashAttribute("registrationError", "Email e senha são obrigatórios.");
            return "redirect:/register";
        }
        if (senha.length() < 6) {
             redirectAttributes.addFlashAttribute("registrationError", "A senha deve ter pelo menos 6 caracteres.");
             redirectAttributes.addFlashAttribute("email", email); // Devolve email para preencher
             return "redirect:/register";
        }
        if (!senha.equals(confirmarSenha)) {
            redirectAttributes.addFlashAttribute("registrationError", "As senhas não coincidem.");
             redirectAttributes.addFlashAttribute("email", email); // Devolve email para preencher
            return "redirect:/register";
        }

        // 2. Tenta registrar (UsuarioService precisa ser ajustado)
        try {
            Usuario novoUsuario = new Usuario();
            novoUsuario.setEmail(email);
            novoUsuario.setSenha(senha); // Service vai encodar

            usuarioService.registerUserBasic(novoUsuario); // ** CHAMA NOVO MÉTODO NO SERVICE **

            // 3. Guarda email na sessão para a próxima etapa
            session.setAttribute("registrationEmail", email); // Chave para identificar o usuário

            // 4. Redireciona para completar o cadastro
            return "redirect:/completar-cadastro";

        } catch (IllegalStateException e) { // Captura erro de email já existente
            redirectAttributes.addFlashAttribute("registrationError", e.getMessage());
             redirectAttributes.addFlashAttribute("email", email); // Devolve email
            return "redirect:/register";
        } catch (Exception e) { // Captura outros erros inesperados
            redirectAttributes.addFlashAttribute("registrationError", "Erro inesperado durante o registro. Tente novamente.");
            return "redirect:/register";
        }
    }
}