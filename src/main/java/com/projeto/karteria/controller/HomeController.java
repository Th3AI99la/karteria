package com.projeto.karteria.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.projeto.karteria.model.TipoUsuario;
import com.projeto.karteria.repository.AnuncioRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {

    @Autowired
    private AnuncioRepository anuncioRepository; 

    @GetMapping("/")
    public String showIndexPage() {
        return "index";
    }

   @GetMapping("/home")
    public String showHomePage(HttpSession session, Model model) {
        TipoUsuario perfilAtivo = (TipoUsuario) session.getAttribute("perfilAtivo");

        if (perfilAtivo == null) {
            return "redirect:/escolher-perfil";
        }

        if (perfilAtivo == TipoUsuario.EMPREGADOR) {
            // Lógica futura para o empregador (ex: listar seus próprios anúncios)
            return "dashboard-empregador";
        } else {
            // Busca todos os anúncios e os adiciona ao modelo para o colaborador
            model.addAttribute("anuncios", anuncioRepository.findAll());
            return "dashboard-colaborador";
        }
    }
}