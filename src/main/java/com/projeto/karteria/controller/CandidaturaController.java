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
import com.projeto.karteria.model.Notificacao;
import com.projeto.karteria.model.Usuario;
import com.projeto.karteria.repository.AnuncioRepository;
import com.projeto.karteria.repository.CandidaturaRepository;
import com.projeto.karteria.repository.NotificacaoRepository;
import com.projeto.karteria.repository.UsuarioRepository;

@Controller
public class CandidaturaController {

    @Autowired
    private CandidaturaRepository candidaturaRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private AnuncioRepository anuncioRepository;
    @Autowired
    private NotificacaoRepository notificacaoRepository;

    @PostMapping("/candidatar/{anuncioId}")
    @PreAuthorize("@activeProfileSecurityService.hasActiveRole('COLABORADOR')")
    public String seCandidatar(@PathVariable Long anuncioId, Authentication authentication,
            RedirectAttributes redirectAttributes) {
        String email = authentication.getName();
        Usuario colaborador = usuarioRepository.findByEmail(email).orElseThrow();
        Anuncio anuncio = anuncioRepository.findById(anuncioId).orElseThrow();

        // Verificação de auto-candidatura
        if (colaborador.getId().equals(anuncio.getAnunciante().getId())) {
            redirectAttributes.addFlashAttribute("erro", "Você não pode se candidatar à sua própria vaga.");
            return "redirect:/anuncios/detalhes/" + anuncioId;
        }

        // Cria a nova candidatura
        Candidatura novaCandidatura = new Candidatura();
        novaCandidatura.setColaborador(colaborador);
        novaCandidatura.setAnuncio(anuncio);
        novaCandidatura.setDataCandidatura(LocalDateTime.now());
        candidaturaRepository.save(novaCandidatura);

        // Criar Notificação para o Empregador **
        try {
            Usuario empregador = anuncio.getAnunciante();
            String mensagem = colaborador.getNome() + " se candidatou para sua vaga '" + anuncio.getTitulo() + "'.";
            // Link que leva para a página de gerenciamento da vaga específica
            String link = "/anuncios/gerenciar/" + anuncio.getId();

            Notificacao notificacao = new Notificacao(empregador, mensagem, link);
            notificacaoRepository.save(notificacao);
        } catch (Exception e) {
            // Logar o erro, mas não impedir a candidatura de funcionar
            System.err.println("Erro ao criar notificação: " + e.getMessage());
            // Considerar usar um logger mais robusto aqui (SLF4j, Logback)
        }
        // ***********************************************

        redirectAttributes.addFlashAttribute("sucesso", "Candidatura realizada com sucesso!");
        return "redirect:/home";
    }
}