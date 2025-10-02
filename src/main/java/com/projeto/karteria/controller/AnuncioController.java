package com.projeto.karteria.controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.projeto.karteria.model.Anuncio;
import com.projeto.karteria.model.Usuario;
import com.projeto.karteria.repository.AnuncioRepository;
import com.projeto.karteria.repository.UsuarioRepository;

@Controller
@RequestMapping("/anuncios")
public class AnuncioController {

    @Autowired
    private AnuncioRepository anuncioRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;

    // Exibe o formulário de criação de anúncio
    @GetMapping("/novo")
    @PreAuthorize("hasAuthority('EMPREGADOR')") // Apenas usuários com este "papel" podem acessar
    public String showAnuncioForm(Model model) {
        model.addAttribute("anuncio", new Anuncio());
        return "anuncio-form";
    }

    // Processa o envio do formulário
    @PostMapping("/salvar")
    @PreAuthorize("hasAuthority('EMPREGADOR')")
    public String salvarAnuncio(@ModelAttribute("anuncio") Anuncio anuncio, Authentication authentication) {
        // Pega o email do usuário logado
        String email = authentication.getName();
        // Busca o usuário no banco de dados
        Usuario usuarioLogado = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Usuário logado não encontrado."));

        // Associa o usuário ao anúncio e define a data de postagem
        anuncio.setAnunciante(usuarioLogado);
        anuncio.setDataPostagem(LocalDateTime.now());

        // Salva o anúncio no banco
        anuncioRepository.save(anuncio);

        return "redirect:/home"; // Redireciona para o dashboard após salvar
    }
}