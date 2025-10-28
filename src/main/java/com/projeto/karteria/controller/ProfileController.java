package com.projeto.karteria.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes; 
import org.springframework.web.bind.annotation.PostMapping; 
import org.springframework.web.bind.annotation.RequestParam; 

import com.projeto.karteria.model.Usuario;
import com.projeto.karteria.repository.UsuarioRepository;

@Controller
public class ProfileController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/perfil")
    public String showProfilePage(Authentication authentication, Model model, RedirectAttributes redirectAttributes) {
        // Verifica se o usuário está autenticado
        if (authentication == null || !authentication.isAuthenticated()) {
            redirectAttributes.addFlashAttribute("erro", "Faça login para acessar seu perfil.");
            return "redirect:/login"; // Redireciona para o login se não estiver autenticado
        }

        String email = authentication.getName();
        // Busca o usuário no banco de dados
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElse(null);

        if (usuario == null) {
            // Caso raro: usuário autenticado mas não encontrado no banco
            redirectAttributes.addFlashAttribute("erro", "Usuário não encontrado.");
            return "redirect:/logout";
        }

        model.addAttribute("usuario", usuario);
        return "profile";
    }

// ** NOVO MÉTODO POST PARA SALVAR EDIÇÃO **
    @PostMapping("/perfil/salvar")
    public String saveProfile(
            // Recebe os campos do formulário individualmente
            @RequestParam String nome,
            @RequestParam String sobrenome,
            // Email e CPF não são enviados pois são readonly/disabled
            @RequestParam String telefone,
            @RequestParam(required = false) String telefone2, // Opcional
            @RequestParam String endereco,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        String emailUsuarioLogado = authentication.getName();
        Usuario usuarioParaAtualizar = usuarioRepository.findByEmail(emailUsuarioLogado)
                .orElse(null);

        if (usuarioParaAtualizar == null) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao encontrar usuário para atualizar.");
            return "redirect:/logout";
        }

        // Validações básicas (poderiam ser mais robustas com Bean Validation)
        if (nome == null || nome.trim().isEmpty() || sobrenome == null || sobrenome.trim().isEmpty() ||
            telefone == null || telefone.trim().isEmpty() || endereco == null || endereco.trim().isEmpty()) {
             redirectAttributes.addFlashAttribute("erro", "Nome, Sobrenome, Telefone 1 e Endereço são obrigatórios.");
             // Poderia redirecionar de volta para /perfil?edit=true para reabrir o modal,
             // mas isso é mais complexo. Por enquanto, só mostra erro na página principal.
             return "redirect:/perfil";
        }

        // Atualiza os campos do objeto Usuario
        usuarioParaAtualizar.setNome(nome.trim());
        usuarioParaAtualizar.setSobrenome(sobrenome.trim());
        usuarioParaAtualizar.setTelefone(telefone.trim());
        usuarioParaAtualizar.setTelefone2(telefone2 != null ? telefone2.trim() : null); // Salva null se vazio
        usuarioParaAtualizar.setEndereco(endereco.trim());
        // Email e CPF não são atualizados

        try {
            usuarioRepository.save(usuarioParaAtualizar); // Salva no banco
            redirectAttributes.addFlashAttribute("sucesso", "Perfil atualizado com sucesso!");
        } catch (Exception e) {
             redirectAttributes.addFlashAttribute("erro", "Erro ao salvar o perfil. Tente novamente.");
             // Logar o erro e.getMessage() seria bom aqui
        }

        return "redirect:/perfil"; // Redireciona de volta para a página de perfil
    }
}
