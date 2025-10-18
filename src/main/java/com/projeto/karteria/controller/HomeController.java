package com.projeto.karteria.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.projeto.karteria.model.Anuncio;
import com.projeto.karteria.model.StatusAnuncio; 
import com.projeto.karteria.model.TipoUsuario;
import com.projeto.karteria.model.Usuario;
import com.projeto.karteria.repository.AnuncioRepository;
import com.projeto.karteria.repository.UsuarioRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {

    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private AnuncioRepository anuncioRepository; 
    
    // MÉTODO PARA MOSTRAR PÁGINA INICIAL
    @GetMapping("/")
    public String showIndexPage() {
        return "index";
    }

    // MÉTODO PARA MOSTRAR PÁGINA HOME BASEADA NO PERFIL ATIVO
    @GetMapping("/home")
    public String showHomePage(HttpSession session, Model model, Authentication authentication) {
        TipoUsuario perfilAtivo = (TipoUsuario) session.getAttribute("perfilAtivo");

        if (perfilAtivo == null) {
            return "redirect:/escolher-perfil";
        }

        if (perfilAtivo == TipoUsuario.EMPREGADOR) {
            String email = authentication.getName();
            Usuario usuarioLogado = usuarioRepository.findByEmail(email).orElseThrow();
            
            // 1. Busca todos os anúncios do usuário
            List<Anuncio> todosAnuncios = anuncioRepository.findByAnuncianteOrderByDataPostagemDesc(usuarioLogado);

            // 2. Filtra os anúncios por status usando Streams
            model.addAttribute("vagasAtivas", todosAnuncios.stream()
                .filter(a -> a.getStatus() == StatusAnuncio.ATIVO)
                .collect(Collectors.toList()));
            
            model.addAttribute("vagasPausadas", todosAnuncios.stream()
                .filter(a -> a.getStatus() == StatusAnuncio.PAUSADO)
                .collect(Collectors.toList()));

            model.addAttribute("vagasArquivadas", todosAnuncios.stream()
                .filter(a -> a.getStatus() == StatusAnuncio.ARQUIVADO)
                .collect(Collectors.toList()));

            return "area-empregador";
        } else {
            model.addAttribute("anuncios", anuncioRepository.findAll());
            return "area-colaborador";
        }
    }
}