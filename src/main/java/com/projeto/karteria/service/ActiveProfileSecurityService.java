package com.projeto.karteria.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory; 
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.projeto.karteria.model.TipoUsuario;

import jakarta.servlet.http.HttpSession;

@Service("activeProfileSecurityService")
public class ActiveProfileSecurityService {

    // ** Adiciona um Logger **
    private static final Logger logger = LoggerFactory.getLogger(ActiveProfileSecurityService.class);

    public boolean hasActiveRole(String requiredProfile) {
        logger.debug("Verificando @PreAuthorize: Perfil requerido = {}", requiredProfile); // LOG 1

        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes(); // Usar getRequestAttributes()

        if (attr == null) {
            logger.warn("WARN: hasActiveRole chamado fora de um contexto de requisição HTTP.");
            return false;
        }

        HttpSession session = attr.getRequest().getSession(false);

        if (session == null) {
            logger.warn("WARN: hasActiveRole - HttpSession não encontrada.");
            return false;
        }

        Object activeProfileAttr = session.getAttribute("perfilAtivo");

        if (activeProfileAttr == null) {
             logger.warn("WARN: hasActiveRole - Atributo 'perfilAtivo' NULO na sessão ID: {}", session.getId()); // LOG 2
             return false;
        }

        if (!(activeProfileAttr instanceof TipoUsuario)) {
             logger.error("ERRO: hasActiveRole - Atributo 'perfilAtivo' não é do tipo TipoUsuario. Tipo encontrado: {}", activeProfileAttr.getClass().getName()); // LOG 3
             return false;
        }

        TipoUsuario activeProfile = (TipoUsuario) activeProfileAttr;
        logger.debug("Perfil ativo encontrado na sessão: {}", activeProfile.name()); // LOG 4

        boolean hasRole = activeProfile.name().equalsIgnoreCase(requiredProfile);
        logger.debug("Resultado da comparação ({} == {}): {}", activeProfile.name(), requiredProfile, hasRole); // LOG 5

        return hasRole;
    }
}