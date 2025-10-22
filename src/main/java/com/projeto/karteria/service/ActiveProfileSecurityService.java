package com.projeto.karteria.service;

import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.projeto.karteria.model.TipoUsuario;

import jakarta.servlet.http.HttpSession;

@Service("activeProfileSecurityService") // Define um nome para o bean ser chamado no SpEL
public class ActiveProfileSecurityService {

    /**
     * Verifica se o perfil ativo na sessão do usuário corresponde ao perfil
     * necessário.
     *
     * @param requiredProfile O nome do TipoUsuario esperado (ex: "EMPREGADOR", "COLABORADOR").
     * @return true se o perfil ativo na sessão for igual ao perfil requerido, false caso contrário ou se não houver sessão/perfil ativo.
     */
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

        // Compara o NOME do enum ativo com a String requerida (ignorando maiúsculas/minúsculas)
        boolean hasRole = activeProfile.name().equalsIgnoreCase(requiredProfile);

        // System.out.println("DEBUG: hasActiveRole check - Required: " + requiredProfile + ", Active: " + activeProfile.name() + ", Result: " + hasRole); // Linha de Debug (remover em produção)

        return hasRole;
    }
}