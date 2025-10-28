package com.projeto.karteria.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult; // Para validação futura
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.projeto.karteria.model.Usuario;
import com.projeto.karteria.repository.UsuarioRepository; // Import necessário

import jakarta.servlet.http.HttpSession;

@SuppressWarnings("unused")
@Controller
public class CompletarCadastroController {

    @Autowired
    private UsuarioRepository usuarioRepository; // Precisa do repo para buscar/salvar

    @GetMapping("/completar-cadastro")
    public String showCompletarCadastroForm(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        String email = (String) session.getAttribute("registrationEmail");

        // Se não houver email na sessão, redireciona para o registro
        if (email == null || email.isEmpty()) {
            redirectAttributes.addFlashAttribute("registrationError",
                    "Sessão inválida. Por favor, registre-se novamente.");
            return "redirect:/register";
        }

        // Cria um objeto Usuario para o formulário (pode pré-preencher email se quiser)
        Usuario usuario = new Usuario();
        usuario.setEmail(email); // Opcional: mostrar email na tela
        model.addAttribute("usuario", usuario);
        model.addAttribute("userEmail", email); // Passa email separado para confirmação visual

        return "completar-cadastro"; // Nome do novo template HTML
    }

    @PostMapping("/completar-cadastro")
    public String processCompletarCadastro(
            @ModelAttribute Usuario usuarioForm, // @Valid pode ser adicionado depois com anotações no Usuario
            BindingResult bindingResult, // Para capturar erros de validação
            HttpSession session,
            RedirectAttributes redirectAttributes,
            Model model) {

        String email = (String) session.getAttribute("registrationEmail");

        // Verifica sessão
        if (bindingResult.hasErrors()) {
            // Agora 'model' está disponível aqui
            model.addAttribute("userEmail", email);
            return "completar-cadastro";
        }
        // Verifica sessão novamente
        if (email == null || email.isEmpty()) {
            redirectAttributes.addFlashAttribute("registrationError",
                    "Sessão inválida. Por favor, registre-se novamente.");
            return "redirect:/register";
        }

        // Se houver erros de validação (ex: campos obrigatórios vazios)
        if (bindingResult.hasErrors()) {
            // Devolve para o formulário com os erros
            model.addAttribute("userEmail", email); // Necessário reenviar para a view
            return "completar-cadastro";
        }

        // Busca o usuário que fez o registro básico
        Usuario usuarioExistente = usuarioRepository.findByEmail(email)
                .orElse(null);

        if (usuarioExistente == null) {
            redirectAttributes.addFlashAttribute("registrationError",
                    "Usuário não encontrado. Por favor, registre-se novamente.");
            session.removeAttribute("registrationEmail"); // Limpa sessão
            return "redirect:/register";
        }

        // Atualiza os dados do usuário existente com os dados do formulário
        usuarioExistente.setNome(usuarioForm.getNome());
        usuarioExistente.setSobrenome(usuarioForm.getSobrenome());
        usuarioExistente.setTelefone(usuarioForm.getTelefone());
        usuarioExistente.setTelefone2(usuarioForm.getTelefone2());
        usuarioExistente.setCpf(usuarioForm.getCpf());
        usuarioExistente.setEndereco(usuarioForm.getEndereco());
        usuarioExistente.setCadastroCompleto(true);

        // Salva as alterações
        usuarioRepository.save(usuarioExistente);

        // Limpa a sessão
        session.removeAttribute("registrationEmail");

        // Redireciona para o login com mensagem de sucesso
        redirectAttributes.addFlashAttribute("registrationSuccess", "Cadastro completo! Faça o login para continuar.");
        return "redirect:/login";
    }
}