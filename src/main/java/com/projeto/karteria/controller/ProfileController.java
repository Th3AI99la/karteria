package com.projeto.karteria.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes; // Import necessário

import com.projeto.karteria.model.Usuario;
import com.projeto.karteria.repository.UsuarioRepository;

@Controller
public class ProfileController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/perfil")
    public String showProfilePage(Authentication authentication, Model model, RedirectAttributes redirectAttributes) {
        // Verifica se o usuário está autenticado
        if (authentication == null || !authentication.isAuthenticated()) {
            redirectAttributes.addFlashAttribute("erro", "Faça login para acessar seu perfil.");
            return "redirect:/login"; // Redireciona para o login se não estiver autenticado
        }

        String email = authentication.getName();
        // Busca o usuário no banco de dados
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElse(null);

        if (usuario == null) {
            // Caso raro: usuário autenticado mas não encontrado no banco
            redirectAttributes.addFlashAttribute("erro", "Usuário não encontrado.");
            return "redirect:/logout";
        }

        model.addAttribute("usuario", usuario);
        return "profile";
    }

    // Futuramente, adicionaremos métodos @GetMapping("/perfil/editar") e
    // @PostMapping("/perfil/salvar") aqui
}