package com.projeto.karteria.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.projeto.karteria.model.Notificacao;
import com.projeto.karteria.model.Usuario;
import com.projeto.karteria.repository.NotificacaoRepository;
import com.projeto.karteria.repository.UsuarioRepository;

@Controller
public class NotificacaoController {

    @Autowired
    private NotificacaoRepository notificacaoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Método auxiliar para pegar o usuário logado
    private Usuario getUsuarioLogado(Authentication authentication) {
        String email = authentication.getName();
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado: " + email));
    }

    /**
     * ETAPA 3: Exibe a página "Ver Todas as Notificações"
     */
    @GetMapping("/notificacoes")
    public String verTodasNotificacoes(Model model, Authentication authentication) {
        Usuario usuario = getUsuarioLogado(authentication);

        // Busca as duas listas
        List<Notificacao> naoLidas = notificacaoRepository.findByUsuarioDestinatarioAndLidaIsFalseOrderByDataCriacaoDesc(usuario);
        List<Notificacao> lidas = notificacaoRepository.findByUsuarioDestinatarioAndLidaIsTrueOrderByDataCriacaoDesc(usuario);

        // Adiciona ao model
        model.addAttribute("notificacoesNaoLidas", naoLidas);
        model.addAttribute("notificacoesLidas", lidas);
        model.addAttribute("contagemNotificacoesNaoLidas", naoLidas.size()); // Para o _navbar

        return "notificacoes"; // Nome do novo arquivo HTML
    }

    /**
     * ETAPA 1: Processa o botão "Limpar Tudo"
     */
    @PostMapping("/notificacoes/marcar-todas-lidas")
    public String marcarTodasComoLidas(Authentication authentication) {
        Usuario usuario = getUsuarioLogado(authentication);
        notificacaoRepository.marcarTodasComoLidas(usuario);
        return "redirect:/home"; // Ou redirecionar para /notificacoes
    }

    /**
     * ETAPA 2: Processa o "X" (Apagar Individualmente) via AJAX
     * @ResponseBody faz este método retornar uma resposta HTTP em vez de um template
     */
    @PostMapping("/notificacoes/marcar-lida/{id}")
    @ResponseBody
    public ResponseEntity<?> marcarComoLida(@PathVariable Long id, Authentication authentication) {
        Usuario usuario = getUsuarioLogado(authentication);
        Optional<Notificacao> notificacaoOpt = notificacaoRepository.findById(id);

        if (notificacaoOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Notificacao notificacao = notificacaoOpt.get();

        // Verifica se a notificação pertence ao usuário logado
        if (!notificacao.getUsuarioDestinatario().getId().equals(usuario.getId())) {
            return ResponseEntity.status(403).body("Acesso negado"); // 403 Forbidden
        }

        notificacao.setLida(true);
        notificacaoRepository.save(notificacao);

        return ResponseEntity.ok().build(); // Retorna 200 OK
    }


    /**
     * ETAPA 4: Processa o botão "Excluir" (da página "Ver Todas")
     */
    @PostMapping("/notificacoes/excluir/{id}")
    @Transactional
    public String excluirNotificacao(@PathVariable Long id, Authentication authentication, RedirectAttributes redirectAttributes) {
        Usuario usuario = getUsuarioLogado(authentication);
        Optional<Notificacao> notificacaoOpt = notificacaoRepository.findById(id);

        if (notificacaoOpt.isPresent()) {
            Notificacao notificacao = notificacaoOpt.get();
            // Verifica se a notificação pertence ao usuário logado
            if (notificacao.getUsuarioDestinatario().getId().equals(usuario.getId())) {
                notificacaoRepository.delete(notificacao);
                redirectAttributes.addFlashAttribute("sucesso", "Notificação excluída com sucesso.");
            } else {
                redirectAttributes.addFlashAttribute("erro", "Não foi possível excluir a notificação.");
            }
        }
        
        return "redirect:/notificacoes"; // Volta para a página de notificações
    }
}