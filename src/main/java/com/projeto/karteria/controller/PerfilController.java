package com.projeto.karteria.controller;

import com.projeto.karteria.model.TipoUsuario;
import com.projeto.karteria.model.Usuario;
import com.projeto.karteria.repository.UsuarioRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class PerfilController {

  @Autowired private UsuarioRepository usuarioRepository;

  @GetMapping("/escolher-perfil")
  public String showEscolherPerfil(Authentication authentication, Model model) {
    // Pega o email do usuário logado
    String email = authentication.getName();
    // Busca o usuário no banco para pegar o nome
    Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);

    if (usuario != null) {
      model.addAttribute("nomeUsuario", usuario.getNome());
    }

    return "escolher-perfil";
  }

  @GetMapping("/acessar/{perfil}")
  public String acessarComo(@PathVariable String perfil, HttpSession session) {
    if ("empregador".equalsIgnoreCase(perfil)) {
      // Salva o perfil escolhido na sessão
      session.setAttribute("perfilAtivo", TipoUsuario.EMPREGADOR);
      return "redirect:/home"; // Redireciona para o dashboard
    }
    if ("colaborador".equalsIgnoreCase(perfil)) {
      // Salva o perfil escolhido na sessão
      session.setAttribute("perfilAtivo", TipoUsuario.COLABORADOR);
      return "redirect:/home"; // Redireciona para o dashboard
    }
    return "redirect:/escolher-perfil"; // Se a URL for inválida, volta para a escolha
  }
}
