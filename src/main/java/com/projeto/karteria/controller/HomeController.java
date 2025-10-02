package com.projeto.karteria.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired; // Importe o serviço
import org.springframework.stereotype.Controller; // Importe o Autowired
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping; // Importe o Model

import com.projeto.karteria.model.Usuario;
import com.projeto.karteria.service.UsuarioService; // Importe a List

@Controller
public class HomeController {

    @Autowired // Injeta a dependência do nosso serviço de usuário
    private UsuarioService usuarioService;

    @GetMapping("/")
    public String showIndexPage() {
        return "index";
    }

    @GetMapping("/home")
    public String showHomePage(Model model) {
        List<Usuario> listaDeUsuarios = usuarioService.listarTodos();

        model.addAttribute("usuarios", listaDeUsuarios);

        return "home";
    }
}