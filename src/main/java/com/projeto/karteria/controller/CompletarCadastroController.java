package com.projeto.karteria.controller;

import com.projeto.karteria.model.Usuario;
import com.projeto.karteria.repository.UsuarioRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class CompletarCadastroController {

  @Autowired private UsuarioRepository usuarioRepository; // Precisa do repo para buscar/salvar

  @GetMapping("/completar-cadastro")
  public String showCompletarCadastroForm(
      HttpSession session, Model model, RedirectAttributes redirectAttributes) {
    String email = (String) session.getAttribute("registrationEmail");

    // Se não houver email na sessão, redireciona para o registro
    if (email == null || email.isEmpty()) {
      redirectAttributes.addFlashAttribute(
          "registrationError", "Sessão inválida. Por favor, registre-se novamente.");
      return "redirect:/register";
    }

    // Cria um objeto Usuario para o formulário (pode pré-preencher email se quiser)
    Usuario usuario = new Usuario();
    usuario.setEmail(email); // Opcional: mostrar email na tela
    model.addAttribute("usuario", usuario);
    model.addAttribute("userEmail", email);

    return "completar-cadastro"; // Nome do novo template HTML
  }

  @PostMapping("/completar-cadastro")
  public String processCompletarCadastro(
      @ModelAttribute Usuario usuarioForm,
      BindingResult bindingResult,
      HttpSession session,
      RedirectAttributes redirectAttributes,
      Model model) {

    String email = (String) session.getAttribute("registrationEmail");

    // Verifica sessão novamente
    if (email == null || email.isEmpty()) {
      redirectAttributes.addFlashAttribute(
          "registrationError", "Sessão inválida. Por favor, registre-se novamente.");
      return "redirect:/register";
    }

    // Se houver erros de validação (ex: campos obrigatórios vazios)
    if (bindingResult.hasErrors()) {
      // Devolve para o formulário com os erros
      model.addAttribute("userEmail", email); // Necessário reenviar para a view
      return "completar-cadastro";
    }

    if (isBlank(usuarioForm.getNome())
        || isBlank(usuarioForm.getSobrenome())
        || isBlank(usuarioForm.getTelefone())
        || isBlank(usuarioForm.getCpf())
        || isBlank(usuarioForm.getEndereco())) {
      model.addAttribute("userEmail", email);
      model.addAttribute("erroCadastro", "Preencha todos os campos obrigatórios para finalizar o cadastro.");
      return "completar-cadastro";
    }

    // Busca o usuário que fez o registro básico
    Usuario usuarioExistente = usuarioRepository.findByEmail(email).orElse(null);

    if (usuarioExistente == null) {
      redirectAttributes.addFlashAttribute(
          "registrationError", "Usuário não encontrado. Por favor, registre-se novamente.");
      session.removeAttribute("registrationEmail"); // Limpa sessão
      return "redirect:/register";
    }

    // Atualiza os dados do usuário existente com os dados do formulário
    usuarioExistente.setNome(usuarioForm.getNome() != null ? usuarioForm.getNome().trim() : null);
    usuarioExistente.setSobrenome(usuarioForm.getSobrenome() != null ? usuarioForm.getSobrenome().trim() : null);
    usuarioExistente.setTelefone(usuarioForm.getTelefone() != null ? usuarioForm.getTelefone().trim() : null);
    usuarioExistente.setTelefone2(usuarioForm.getTelefone2() != null ? usuarioForm.getTelefone2().trim() : null);
    usuarioExistente.setCpf(usuarioForm.getCpf() != null ? usuarioForm.getCpf().trim() : null);
    usuarioExistente.setEndereco(usuarioForm.getEndereco() != null ? usuarioForm.getEndereco().trim() : null);
    usuarioExistente.setCadastroCompleto(true);

    // Salva as alterações
    usuarioRepository.save(usuarioExistente);

    // Limpa a sessão
    session.removeAttribute("registrationEmail");

    // Redireciona para o login com mensagem de sucesso
    redirectAttributes.addFlashAttribute(
        "registrationSuccess", "Cadastro completo! Faça o login para continuar.");
    return "redirect:/login";
  }

  private boolean isBlank(String valor) {
    return valor == null || valor.trim().isEmpty();
  }
}
