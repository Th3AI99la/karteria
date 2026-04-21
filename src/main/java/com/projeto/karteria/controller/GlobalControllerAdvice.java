package com.projeto.karteria.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.projeto.karteria.model.Notificacao;
import com.projeto.karteria.model.Usuario;
import com.projeto.karteria.repository.NotificacaoRepository;
import com.projeto.karteria.repository.UsuarioRepository;
import com.projeto.karteria.service.ActiveProfileSecurityService;

@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired 
    private NotificacaoRepository notificacaoRepository;
    
    @Autowired 
    private UsuarioRepository usuarioRepository;
    
    @Autowired 
    private ActiveProfileSecurityService activeProfileSecurityService;

    // 1. INJETA A LISTA FILTRADA NO MENU SUSPENSO (Esconde o link proibido)
    @ModelAttribute("notificacoesNaoLidas")
    public List<Notificacao> getNotificacoesNaoLidasGlobais(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) return List.of();
        
        Usuario usuario = usuarioRepository.findByEmail(auth.getName()).orElse(null);
        if (usuario == null) return List.of();

        boolean isColaborador = activeProfileSecurityService.hasActiveRole("COLABORADOR");
        List<Notificacao> lista = notificacaoRepository.findByUsuarioDestinatarioAndLidaIsFalseOrderByDataCriacaoDesc(usuario);

        return lista.stream().filter(n -> {
            if (n.getLink() == null) return true;
            // Se for Colaborador, a notificação com link de /gerenciar/ some da lista!
            if (isColaborador && n.getLink().contains("/gerenciar")) return false;
            return true;
        }).toList();
    }

    // 2. INJETA A CONTAGEM FILTRADA NO SININHO
    @ModelAttribute("contagemNotificacoesNaoLidas")
    public int getContagemGlobal(Authentication auth) {
        return getNotificacoesNaoLidasGlobais(auth).size();
    }
}