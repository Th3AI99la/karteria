package com.projeto.karteria.controller;

import com.projeto.karteria.model.Notificacao;
import com.projeto.karteria.model.Usuario;
import com.projeto.karteria.repository.NotificacaoRepository;
import com.projeto.karteria.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
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
        if (!isUsuarioAutenticado(auth)) return List.of();
        
        Usuario usuario = usuarioRepository.findByEmail(auth.getName()).orElse(null);
        if (usuario == null) return List.of();

        return notificacaoRepository.findByUsuarioDestinatarioAndLidaIsFalseOrderByDataCriacaoDesc(usuario);
    }

    // INJETA A CONTAGEM NO SININHO
    @ModelAttribute("contagemNotificacoesNaoLidas")
    public long getContagemGlobal(Authentication auth) {
        if (!isUsuarioAutenticado(auth)) return 0;

        Usuario usuario = usuarioRepository.findByEmail(auth.getName()).orElse(null);
        if (usuario == null) return 0;

        return notificacaoRepository.countByUsuarioDestinatarioAndLidaIsFalse(usuario);
    }

    // INJETA O ID DO USUÁRIO NA SESSÃO PARA O WEBSOCKET
    @ModelAttribute("usuarioLogadoId")
    public Long getUsuarioLogadoId(Authentication auth) {
        if (!isUsuarioAutenticado(auth)) return null;
        Usuario usuario = usuarioRepository.findByEmail(auth.getName()).orElse(null);
        return usuario != null ? usuario.getId() : null;
    }

    private boolean isUsuarioAutenticado(Authentication auth) {
        return auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken);
    }
}
