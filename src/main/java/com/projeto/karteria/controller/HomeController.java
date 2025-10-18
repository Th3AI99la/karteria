package com.projeto.karteria.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.projeto.karteria.model.TipoUsuario;
import com.projeto.karteria.model.Usuario;
import com.projeto.karteria.repository.AnuncioRepository;
import com.projeto.karteria.repository.UsuarioRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {

    @Autowired private UsuarioRepository usuarioRepository;

    @Autowired
    private AnuncioRepository anuncioRepository; 

    @GetMapping("/")
    public String showIndexPage() {
        return "index";
    }

    @GetMapping("/home")
    public String showHomePage(HttpSession session, Model model, Authentication authentication) {
        TipoUsuario perfilAtivo = (TipoUsuario) session.getAttribute("perfilAtivo");

        if (perfilAtivo == null) {
            return "redirect:/escolher-perfil";
        }

        if (perfilAtivo == TipoUsuario.EMPREGADOR) {
            String email = authentication.getName();
            Usuario usuarioLogado = usuarioRepository.findByEmail(email).orElseThrow();
            // Busca apenas os anúncios do usuário logado e os adiciona ao modelo
            model.addAttribute("anunciosDoUsuario", anuncioRepository.findByAnuncianteOrderByDataPostagemDesc(usuarioLogado));
            return "area-empregador";
        } else {
            // Busca todos os anúncios e os adiciona ao modelo para o colaborador
            model.addAttribute("anuncios", anuncioRepository.findAll());
            return "area-colaborador";
        }
    }
}