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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    // Método para MOSTRAR a página do formulário
    @GetMapping("/novo")
    @PreAuthorize("hasAuthority('EMPREGADOR')") // Apenas Empregadores podem ver esta página
    public String showAnuncioForm(Model model) {
        model.addAttribute("anuncio", new Anuncio());
        return "anuncio-form";
    }

    // Método para SALVAR os dados do formulário
    @PostMapping("/salvar")
    @PreAuthorize("hasAuthority('EMPREGADOR')") // Apenas Empregadores podem executar esta ação
    public String salvarAnuncio(@ModelAttribute Anuncio anuncio, Authentication authentication, RedirectAttributes redirectAttributes) {
        // Pega o usuário que está logado
        String email = authentication.getName();
        Usuario usuarioLogado = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Usuário logado não encontrado."));

        // Associa o usuário ao anúncio e define a data
        anuncio.setAnunciante(usuarioLogado);
        anuncio.setDataPostagem(LocalDateTime.now());

        // Salva o anúncio no banco de dados
        anuncioRepository.save(anuncio);
        
        // Adiciona uma mensagem de sucesso para ser exibida no dashboard
        redirectAttributes.addFlashAttribute("sucesso", "Vaga publicada com sucesso!");

        return "redirect:/home"; // Redireciona para a home (que levará ao dashboard)
    }
}