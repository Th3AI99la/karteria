package com.projeto.karteria.controller;

import com.projeto.karteria.model.Notificacao;
import com.projeto.karteria.model.Usuario;
import com.projeto.karteria.repository.NotificacaoRepository;
import com.projeto.karteria.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import java.util.List;

@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired 
    private NotificacaoRepository notificacaoRepository;
    
    @Autowired 
    private UsuarioRepository usuarioRepository;

    // INJETA A LISTA NO MENU SUSPENSO (Sem filtros de invisibilidade)
    @ModelAttribute("notificacoesNaoLidas")
    public List<Notificacao> getNotificacoesNaoLidasGlobais(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) return List.of();
        
        Usuario usuario = usuarioRepository.findByEmail(auth.getName()).orElse(null);
        if (usuario == null) return List.of();

        return notificacaoRepository.findByUsuarioDestinatarioAndLidaIsFalseOrderByDataCriacaoDesc(usuario);
    }

    // INJETA A CONTAGEM NO SININHO
    @ModelAttribute("contagemNotificacoesNaoLidas")
    public int getContagemGlobal(Authentication auth) {
        return getNotificacoesNaoLidasGlobais(auth).size();
    }

    // INJETA O ID DO USUÁRIO NA SESSÃO PARA O WEBSOCKET
    @ModelAttribute("usuarioLogadoId")
    public Long getUsuarioLogadoId(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) return null;
        Usuario usuario = usuarioRepository.findByEmail(auth.getName()).orElse(null);
        return usuario != null ? usuario.getId() : null;
    }
}