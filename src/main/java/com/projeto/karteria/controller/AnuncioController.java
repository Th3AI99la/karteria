package com.projeto.karteria.controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired; // <-- IMPORT NECESSÁRIO
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.projeto.karteria.model.Anuncio; // <-- IMPORT NECESSÁRIO
import com.projeto.karteria.model.StatusAnuncio;
import com.projeto.karteria.model.Usuario;
import com.projeto.karteria.repository.AnuncioRepository;
import com.projeto.karteria.repository.UsuarioRepository;

@Controller
@RequestMapping("/anuncios")
public class AnuncioController {

    @Autowired private AnuncioRepository anuncioRepository;
    @Autowired private UsuarioRepository usuarioRepository;

    @GetMapping("/novo")
    @PreAuthorize("hasAuthority('EMPREGADOR')")
    public String showAnuncioForm(Model model) {
        model.addAttribute("anuncio", new Anuncio());
        return "anuncio-form";
    }

    @PostMapping("/salvar")
    @PreAuthorize("hasAuthority('EMPREGADOR')")
    public String salvarAnuncio(@ModelAttribute Anuncio anuncio, Authentication authentication, RedirectAttributes redirectAttributes) {
        String email = authentication.getName();
        Usuario usuarioLogado = usuarioRepository.findByEmail(email).orElseThrow();

        if (anuncio.getId() == null) {
            anuncio.setAnunciante(usuarioLogado);
            anuncio.setDataPostagem(LocalDateTime.now());
            anuncio.setStatus(StatusAnuncio.ATIVO); // Agora funciona
            redirectAttributes.addFlashAttribute("sucesso", "Vaga publicada com sucesso!");
        } else {
            Anuncio anuncioExistente = anuncioRepository.findById(anuncio.getId()).orElseThrow();
            anuncio.setAnunciante(anuncioExistente.getAnunciante());
            anuncio.setDataPostagem(anuncioExistente.getDataPostagem());
            anuncio.setStatus(anuncioExistente.getStatus()); // Agora funciona
            redirectAttributes.addFlashAttribute("sucesso", "Vaga atualizada com sucesso!");
        }

        anuncioRepository.save(anuncio);
        return "redirect:/home";
    }

    @GetMapping("/editar/{id}")
    @PreAuthorize("hasAuthority('EMPREGADOR')")
    public String showEditForm(@PathVariable Long id, Model model, Authentication authentication) {
        Anuncio anuncio = anuncioRepository.findById(id).orElseThrow();
        if (!anuncio.getAnunciante().getEmail().equals(authentication.getName())) {
            return "redirect:/home";
        }
        model.addAttribute("anuncio", anuncio);
        return "anuncio-form";
    }

    @PostMapping("/apagar/{id}")
    @PreAuthorize("hasAuthority('EMPREGADOR')")
    public String apagarAnuncio(@PathVariable Long id, Authentication authentication, RedirectAttributes redirectAttributes) {
        Anuncio anuncio = anuncioRepository.findById(id).orElseThrow();
        if (!anuncio.getAnunciante().getEmail().equals(authentication.getName())) {
            return "redirect:/home";
        }
        anuncioRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("sucesso", "Vaga apagada com sucesso.");
        return "redirect:/home";
    }
    
    @PostMapping("/status/{id}")
    @PreAuthorize("hasAuthority('EMPREGADOR')")
    public String alterarStatusAnuncio(@PathVariable Long id, Authentication authentication, RedirectAttributes redirectAttributes) {
        Anuncio anuncio = anuncioRepository.findById(id).orElseThrow();
        if (!anuncio.getAnunciante().getEmail().equals(authentication.getName())) {
            return "redirect:/home";
        }

        if (anuncio.getStatus() == StatusAnuncio.ATIVO) {
            anuncio.setStatus(StatusAnuncio.PAUSADO); 
            redirectAttributes.addFlashAttribute("sucesso", "Vaga pausada.");
        } else if (anuncio.getStatus() == StatusAnuncio.PAUSADO) {
            anuncio.setStatus(StatusAnuncio.ATIVO);
            redirectAttributes.addFlashAttribute("sucesso", "Vaga reativada.");
        }
        
        anuncioRepository.save(anuncio);
        return "redirect:/home";
    }
}