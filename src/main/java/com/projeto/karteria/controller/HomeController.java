package com.projeto.karteria.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.projeto.karteria.model.TipoUsuario;

import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {

    @GetMapping("/")
    public String showIndexPage() {
        return "index";
    }

    @GetMapping("/home")
    public String showHomePage(HttpSession session) {
        // Verifica qual perfil está ativo na sessão
        TipoUsuario perfilAtivo = (TipoUsuario) session.getAttribute("perfilAtivo");

        if (perfilAtivo == null) {
            // Se nenhum perfil foi escolhido, força o usuário a escolher
            return "redirect:/escolher-perfil";
        }

        if (perfilAtivo == TipoUsuario.EMPREGADOR) {
            return "dashboard-empregador"; // Mostra o dashboard do empregador
        } else {
            return "dashboard-colaborador"; // Mostra o dashboard do colaborador
        }
    }
}