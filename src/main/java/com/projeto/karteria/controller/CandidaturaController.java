package com.projeto.karteria.controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.projeto.karteria.model.Anuncio;
import com.projeto.karteria.model.Candidatura;
import com.projeto.karteria.model.Usuario;
import com.projeto.karteria.repository.AnuncioRepository;
import com.projeto.karteria.repository.CandidaturaRepository;
import com.projeto.karteria.repository.UsuarioRepository;

@Controller
public class CandidaturaController {

    @Autowired 
    private CandidaturaRepository candidaturaRepository;

    @Autowired 
    private UsuarioRepository usuarioRepository;

    @Autowired 
    private AnuncioRepository anuncioRepository;

    @PostMapping("/candidatar/{anuncioId}")
    @PreAuthorize("hasAuthority('COLABORADOR')") // Apenas colaboradores podem se candidatar
    public String seCandidatar(
            @PathVariable Long anuncioId,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        // Busca o usuário logado (colaborador)
        String email = authentication.getName();
        Usuario colaborador = usuarioRepository.findByEmail(email).orElseThrow();

        // Busca o anúncio ao qual ele está se candidatando
        Anuncio anuncio = anuncioRepository.findById(anuncioId).orElseThrow();

        // ✅ NOVA VERIFICAÇÃO: impedir auto-candidatura
        if (colaborador.getId().equals(anuncio.getAnunciante().getId())) {
            redirectAttributes.addFlashAttribute(
                "erro", 
                "Você não pode se candidatar à sua própria vaga."
            );
            // Redireciona de volta para os detalhes da vaga
            return "redirect:/anuncios/detalhes/" + anuncioId;
        }

        // Cria a nova candidatura
        Candidatura novaCandidatura = new Candidatura();
        novaCandidatura.setColaborador(colaborador);
        novaCandidatura.setAnuncio(anuncio);
        novaCandidatura.setDataCandidatura(LocalDateTime.now());

        // Salva no banco de dados
        candidaturaRepository.save(novaCandidatura);

        // Mensagem de sucesso e redirecionamento
        redirectAttributes.addFlashAttribute("sucesso", "Candidatura realizada com sucesso!");
        return "redirect:/home";
    }
}
