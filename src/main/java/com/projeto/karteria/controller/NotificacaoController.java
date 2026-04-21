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
import com.projeto.karteria.service.ActiveProfileSecurityService;

@Controller
public class NotificacaoController {

    @Autowired
    private NotificacaoRepository notificacaoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ActiveProfileSecurityService activeProfileSecurityService;

    /**
     * ETAPA 1: Método auxiliar para pegar o usuário logado
     */
    private Usuario getUsuarioLogado(Authentication authentication) {
        String email = authentication.getName();
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado: " + email));
    }

    /**
     * ETAPA 2: Processa o "X" (marcar como lida via AJAX)
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

        // Segurança: garante que a notificação pertence ao usuário
        if (!notificacao.getUsuarioDestinatario().getId().equals(usuario.getId())) {
            return ResponseEntity.status(403).body("Acesso negado");
        }

        notificacao.setLida(true);
        notificacaoRepository.save(notificacao);

        return ResponseEntity.ok().build();
    }

    /**
     * ETAPA 3: Exibe a página "Ver Todas as Notificações"
     */
    @GetMapping("/notificacoes")
    public String verTodasNotificacoes(Model model, Authentication authentication) {
        Usuario usuario = getUsuarioLogado(authentication);

        // Correção: verificação por boolean (perfil ativo)
        boolean isColaborador = activeProfileSecurityService.hasActiveRole("COLABORADOR");
        boolean isEmpregador = activeProfileSecurityService.hasActiveRole("EMPREGADOR");

        List<Notificacao> naoLidas = notificacaoRepository
                .findByUsuarioDestinatarioAndLidaIsFalseOrderByDataCriacaoDesc(usuario);

        List<Notificacao> lidas = notificacaoRepository
                .findByUsuarioDestinatarioAndLidaIsTrueOrderByDataCriacaoDesc(usuario);

        // Filtro inteligente por perfil
        naoLidas = filtrarPorPerfil(naoLidas, isColaborador, isEmpregador);
        lidas = filtrarPorPerfil(lidas, isColaborador, isEmpregador);

        model.addAttribute("notificacoesNaoLidas", naoLidas);
        model.addAttribute("notificacoesLidas", lidas);
        model.addAttribute("contagemNotificacoesNaoLidas", naoLidas.size());

        return "notificacoes";
    }

    /**
     * ETAPA 3.1: Método auxiliar para filtrar notificações por perfil
     */
    private List<Notificacao> filtrarPorPerfil(List<Notificacao> lista, boolean isColaborador, boolean isEmpregador) {
        return lista.stream().filter(n -> {
            if (n.getLink() == null)
                return true;

            // Colaborador não pode ver links de gerenciar
            if (isColaborador && n.getLink().contains("/gerenciar"))
                return false;

            // Empregador não pode ver links de candidatar
            if (isEmpregador && n.getLink().contains("/candidatar"))
                return false;

            return true;
        }).toList();
    }

    /**
     * ETAPA 4: Excluir notificação (página completa)
     */
    @PostMapping("/notificacoes/excluir/{id}")
    @Transactional
    public String excluirNotificacao(@PathVariable Long id, Authentication authentication,
            RedirectAttributes redirectAttributes) {

        Usuario usuario = getUsuarioLogado(authentication);
        Optional<Notificacao> notificacaoOpt = notificacaoRepository.findById(id);

        if (notificacaoOpt.isPresent()) {
            Notificacao notificacao = notificacaoOpt.get();

            // Segurança: garante que pertence ao usuário
            if (notificacao.getUsuarioDestinatario().getId().equals(usuario.getId())) {
                notificacaoRepository.delete(notificacao);
                redirectAttributes.addFlashAttribute("sucesso", "Notificação excluída com sucesso.");
            } else {
                redirectAttributes.addFlashAttribute("erro", "Não foi possível excluir a notificação.");
            }
        }

        return "redirect:/notificacoes";
    }

    /// ETAPA 5: Redirecionar ao clicar na notificação (marcar como lida +
    /// redirecionar)
    @GetMapping("/notificacoes/ir/{id}")
    public String redirecionarNotificacao(@PathVariable Long id) {
        Notificacao notificacao = notificacaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notificação não encontrada"));

        // Marca como lida
        notificacao.setLida(true);
        notificacaoRepository.save(notificacao);

        // Pega o link original (ex: /anuncios/gerenciar/10) e redireciona
        String destino = notificacao.getLink();
        return "redirect:" + (destino != null ? destino : "/home");
    }
}