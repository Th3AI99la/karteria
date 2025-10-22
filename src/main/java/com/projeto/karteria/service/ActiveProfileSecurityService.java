package com.projeto.karteria.service;

import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.projeto.karteria.model.TipoUsuario;

import jakarta.servlet.http.HttpSession;

@Service("activeProfileSecurityService") 
public class ActiveProfileSecurityService {

    @SuppressWarnings("unused")
    public boolean hasActiveRole(String requiredProfile) {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        // Verifica se estamos em um contexto de requisição HTTP
        if (attr == null) {
            System.err.println("WARN: ActiveProfileSecurityService chamado fora de um contexto de requisição HTTP.");
            return false;
        }
        HttpSession session = attr.getRequest().getSession(false); // Pega a sessão SE ela existir

        // Se não houver sessão ou perfil ativo definido nela, nega o acesso
        if (session == null || session.getAttribute("perfilAtivo") == null) {
            System.err.println("WARN: ActiveProfileSecurityService - Sessão ou perfilAtivo nulo.");
            return false;
        }

        // Pega o perfil ativo da sessão
        TipoUsuario activeProfile = (TipoUsuario) session.getAttribute("perfilAtivo");

        // Compara o NOME do enum ativo com a String requerida (ignorando
        // maiúsculas/minúsculas)
        boolean hasRole = activeProfile.name().equalsIgnoreCase(requiredProfile);

        // System.out.println("DEBUG: hasActiveRole check - Required: " +
        // requiredProfile + ", Active: " + activeProfile.name() + ", Result: " +
        // hasRole); // Linha de Debug (remover em produção)

        return hasRole;
    }
}