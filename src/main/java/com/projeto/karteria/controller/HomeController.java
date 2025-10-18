package com.projeto.karteria.controller;

import java.util.ArrayList; 
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

        // --- LÓGICA DO EMPREGADOR ---
        if (perfilAtivo == TipoUsuario.EMPREGADOR) {
            String email = authentication.getName();
            Usuario usuarioLogado = usuarioRepository.findByEmail(email).orElseThrow();

            List<Anuncio> todosAnunciosDoUsuario = anuncioRepository.findByAnuncianteOrderByDataPostagemDesc(usuarioLogado);

            // Filtra para as abas 
            List<Anuncio> vagasAtivas = todosAnunciosDoUsuario.stream()
                .filter(a -> a.getStatus() == StatusAnuncio.ATIVO)
                .collect(Collectors.toList());
            List<Anuncio> vagasPausadas = todosAnunciosDoUsuario.stream()
                .filter(a -> a.getStatus() == StatusAnuncio.PAUSADO)
                .collect(Collectors.toList());
            List<Anuncio> vagasArquivadas = todosAnunciosDoUsuario.stream()
                .filter(a -> a.getStatus() == StatusAnuncio.ARQUIVADO)
                .collect(Collectors.toList());

            model.addAttribute("vagasAtivas", vagasAtivas);
            model.addAttribute("vagasPausadas", vagasPausadas);
            model.addAttribute("vagasArquivadas", vagasArquivadas);

            // Combina todas as vagas em uma única lista para gerar os modais 
            List<Anuncio> todasAsVagas = new ArrayList<>();
            todasAsVagas.addAll(vagasAtivas);
            todasAsVagas.addAll(vagasPausadas);
            todasAsVagas.addAll(vagasArquivadas); 
            model.addAttribute("todasAsVagas", todasAsVagas);
            // ************************************************************************

            return "area-empregador";
        }
        // LÓGICA DO COLABORADOR 
        else {
            model.addAttribute("anuncios", anuncioRepository.findAll());
            return "area-colaborador";
        }
    }
}