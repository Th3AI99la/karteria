package com.projeto.karteria.controller;

import com.projeto.karteria.model.Usuario;
import com.projeto.karteria.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ProfileController {

  @Autowired private UsuarioRepository usuarioRepository;

  @GetMapping("/perfil")
  public String showProfilePage(
      Authentication authentication, Model model, RedirectAttributes redirectAttributes) {
    if (authentication == null || !authentication.isAuthenticated()) {
      redirectAttributes.addFlashAttribute("erro", "Faça login para acessar seu perfil.");
      return "redirect:/login";
    }

    String email = authentication.getName();
    Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);

    if (usuario == null) {
      redirectAttributes.addFlashAttribute("erro", "Usuário não encontrado.");
      return "redirect:/logout";
    }

    model.addAttribute("usuario", usuario);
    return "profile";
  }

  // Método para processar o formulário de atualização de perfil
  @PostMapping("/perfil/salvar")
  public String saveProfile(
      @RequestParam String nome,
      @RequestParam String sobrenome,
      @RequestParam String telefone,
      @RequestParam(required = false) String telefone2,
      @RequestParam String endereco, // <-- Recebe do input (atualizado pelo JS)
      Authentication authentication,
      RedirectAttributes redirectAttributes) {

    if (authentication == null || !authentication.isAuthenticated()) {
      return "redirect:/login";
    }

    String emailUsuarioLogado = authentication.getName();
    Usuario usuarioParaAtualizar = usuarioRepository.findByEmail(emailUsuarioLogado).orElse(null);

    if (usuarioParaAtualizar == null) {
      redirectAttributes.addFlashAttribute("erro", "Erro ao encontrar usuário para atualizar.");
      return "redirect:/logout";
    }

    // Validação básica de campos obrigatórios
    if (nome.trim().isEmpty()
        || sobrenome.trim().isEmpty()
        || telefone.trim().isEmpty()
        || endereco.trim().isEmpty()) {
      redirectAttributes.addFlashAttribute(
          "erro", "Nome, Sobrenome, Telefone 1 e Endereço são obrigatórios.");
      return "redirect:/perfil"; // Volta para a pág. de perfil com erro
    }

    // Atualiza os campos do usuário
    usuarioParaAtualizar.setNome(nome.trim());
    usuarioParaAtualizar.setSobrenome(sobrenome.trim());
    usuarioParaAtualizar.setTelefone(telefone.trim());
    usuarioParaAtualizar.setTelefone2(telefone2 != null ? telefone2.trim() : null);
    usuarioParaAtualizar.setEndereco(endereco.trim());

    try {
      // Salva o objeto atualizado no banco de dados
      usuarioRepository.save(usuarioParaAtualizar);
      redirectAttributes.addFlashAttribute("sucesso", "Perfil atualizado com sucesso!");
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("erro", "Erro ao salvar o perfil. Tente novamente.");
    }

    // Redireciona de volta para a página de perfil
    return "redirect:/perfil";
  }
}
