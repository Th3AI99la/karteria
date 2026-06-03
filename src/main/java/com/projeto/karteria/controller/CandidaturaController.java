package com.projeto.karteria.controller;

import com.projeto.karteria.model.Anuncio;
import com.projeto.karteria.model.Candidatura;
import com.projeto.karteria.model.Notificacao;
import com.projeto.karteria.model.StatusAnuncio;
import com.projeto.karteria.model.Usuario;
import com.projeto.karteria.repository.AnuncioRepository;
import com.projeto.karteria.repository.CandidaturaRepository;
import com.projeto.karteria.repository.NotificacaoRepository;
import com.projeto.karteria.repository.UsuarioRepository;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class CandidaturaController {

    private static final Logger logger = LoggerFactory.getLogger(CandidaturaController.class);

    @Autowired 
    private CandidaturaRepository candidaturaRepository;
    
    @Autowired 
    private UsuarioRepository usuarioRepository;
    
    @Autowired 
    private AnuncioRepository anuncioRepository;
    
    @Autowired 
    private NotificacaoRepository notificacaoRepository;
    
    @Autowired 
    private SimpMessagingTemplate messagingTemplate;

    @PostMapping("/candidatar/{anuncioId}")
    @PreAuthorize("@activeProfileSecurityService.hasActiveRole('COLABORADOR')")
    public String seCandidatar(
            @PathVariable Long anuncioId,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        
        String email = authentication.getName();
        Usuario colaborador = usuarioRepository.findByEmail(email).orElseThrow();
        Anuncio anuncio = anuncioRepository.findById(anuncioId).orElseThrow();

        if (anuncio.getStatus() != StatusAnuncio.ATIVO) {
            redirectAttributes.addFlashAttribute("erro", "Esta vaga não está aberta para candidaturas.");
            return "redirect:/anuncios/detalhes/" + anuncioId;
        }

        if (anuncio.getAnunciante() == null) {
            redirectAttributes.addFlashAttribute("erro", "Não foi possível identificar o anunciante desta vaga.");
            return "redirect:/anuncios/detalhes/" + anuncioId;
        }

        // 1. Verificação de auto-candidatura
        if (colaborador.getId().equals(anuncio.getAnunciante().getId())) {
            redirectAttributes.addFlashAttribute(
                "erro", "Você não pode se candidatar à sua própria vaga.");
            return "redirect:/anuncios/detalhes/" + anuncioId;
        }

        // 2. Evita Candidatura duplicada
        boolean jaCandidatado = candidaturaRepository.existsByColaboradorAndAnuncio(colaborador, anuncio);
        if (jaCandidatado) {
            redirectAttributes.addFlashAttribute("erro", "Você já se candidatou para esta vaga.");
            return "redirect:/anuncios/detalhes/" + anuncioId;
        }

        // 3. Cria e salva a nova candidatura
        Candidatura novaCandidatura = new Candidatura();
        novaCandidatura.setColaborador(colaborador);
        novaCandidatura.setAnuncio(anuncio);
        novaCandidatura.setDataCandidatura(LocalDateTime.now());
        candidaturaRepository.save(novaCandidatura);

        // 4. Fluxo de Notificação (Banco + Tempo Real)
        try {
            Usuario empregador = anuncio.getAnunciante();
            String mensagem = colaborador.getNome() + " se candidatou para sua vaga '" + anuncio.getTitulo() + "'.";
            String link = "/anuncios/gerenciar/" + anuncio.getId();

            // Salva no banco de dados para consulta posterior (F5)
            Notificacao notificacao = new Notificacao(empregador, mensagem, link);
            notificacaoRepository.save(notificacao);

            // Prepara o payload leve para o WebSocket (Evita erro de recursão do Jackson)
            Map<String, String> payload = new HashMap<>();
            payload.put("mensagem", mensagem);
            payload.put("link", link);

            // Envia para o canal específico do empregador
            messagingTemplate.convertAndSend("/topic/notificacoes/" + empregador.getId(), payload);

        } catch (RuntimeException e) {
            logger.warn("Erro ao processar notificação em tempo real", e);
        }
        
        redirectAttributes.addFlashAttribute("sucesso", "Candidatura realizada com sucesso!");
        
        return "redirect:/anuncios/detalhes/" + anuncioId;
    }
}
