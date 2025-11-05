package com.projeto.karteria.service;

import com.projeto.karteria.model.TipoUsuario;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service("activeProfileSecurityService")
public class ActiveProfileSecurityService {

  // Logger para registrar mensagens de depuração e erro
  private static final Logger logger = LoggerFactory.getLogger(ActiveProfileSecurityService.class);

  public boolean hasActiveRole(String requiredProfile) {
    logger.debug("Verificando @PreAuthorize: Perfil requerido = {}", requiredProfile);

    ServletRequestAttributes attr =
        (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

    // Verifica se estamos em um contexto de requisição web
    if (attr == null) {
      logger.warn("WARN: hasActiveRole chamado fora de um contexto de requisição HTTP.");
      return false; // Não pode verificar sem requisição
    }

    // Tenta pegar a sessão HTTP atual (sem criar uma nova se não existir)
    HttpSession session = attr.getRequest().getSession(false);

    // Verifica se a sessão existe
    if (session == null) {
      logger.warn(
          "WARN: hasActiveRole - HttpSession não encontrada. O usuário provavelmente não está logado ou a sessão expirou.");
      return false;
    }

    Object activeProfileAttr = session.getAttribute("perfilAtivo");

    // Verifica se o atributo existe na sessão
    if (activeProfileAttr == null) {
      logger.warn(
          "WARN: hasActiveRole - Atributo 'perfilAtivo' NULO na sessão ID: {}. O usuário selecionou um perfil?",
          session.getId()); // LOG 2
      return false; // Não pode verificar sem o atributo
    }

    // Verifica se o atributo é do tipo esperado (TipoUsuario enum)
    if (!(activeProfileAttr instanceof TipoUsuario)) {
      logger.error(
          "ERRO: hasActiveRole - Atributo 'perfilAtivo' não é do tipo TipoUsuario. Tipo encontrado: {}",
          activeProfileAttr.getClass().getName()); // LOG 3
      return false; // Algo muito errado aconteceu se não for o enum
    }

    TipoUsuario activeProfile = (TipoUsuario) activeProfileAttr;

    // LOG 4: Mostra qual perfil foi LIDO da sessão
    logger.debug("Perfil ativo encontrado na sessão: {}", activeProfile.name());

    boolean hasRole = activeProfile.name().equalsIgnoreCase(requiredProfile);
    // LOG 5: Mostra o resultado da comparação
    logger.debug(
        "Resultado da comparação (Perfil da Sessão [{}] == Perfil Requerido [{}]) -> {}",
        activeProfile.name(),
        requiredProfile,
        hasRole);

    return hasRole;
  }
}
