package com.projeto.karteria.controller;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.projeto.karteria.model.Anuncio;
import com.projeto.karteria.model.Candidatura;
import com.projeto.karteria.model.StatusAnuncio;
import com.projeto.karteria.model.Usuario;
import com.projeto.karteria.repository.AnuncioRepository;
import com.projeto.karteria.repository.CandidaturaRepository;
import com.projeto.karteria.repository.UsuarioRepository;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/anuncios")
public class AnuncioController {

    private static final Logger log = LoggerFactory.getLogger(AnuncioController.class);

    @Autowired
    private AnuncioRepository anuncioRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CandidaturaRepository candidaturaRepository;

    // ================= Helper =================
    private boolean isAnunciante(Anuncio anuncio, Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) return false;
        String email = auth.getName();
        return anuncio.getAnunciante() != null && email.equals(anuncio.getAnunciante().getEmail());
    }

    // ================= CRUD ANÚNCIO =================

    @GetMapping("/novo")
    @PreAuthorize("@activeProfileSecurityService.hasActiveRole('EMPREGADOR')")
    public String showAnuncioForm(Model model) {
        model.addAttribute("anuncio", new Anuncio());
        return "anuncio-form";
    }

    @PostMapping("/salvar")
    @PreAuthorize("hasAuthority('EMPREGADOR')")
    public String salvarAnuncio(@ModelAttribute Anuncio anuncioForm,
                                Authentication authentication,
                                RedirectAttributes redirectAttributes) {

        Usuario usuarioLogado = usuarioRepository.findByEmail(authentication.getName()).orElseThrow();

        if (anuncioForm.getId() == null) { // NOVO ANÚNCIO
            anuncioForm.setAnunciante(usuarioLogado);
            anuncioForm.setDataPostagem(LocalDateTime.now());
            anuncioForm.setStatus(StatusAnuncio.ATIVO);
            anuncioRepository.save(anuncioForm);
            redirectAttributes.addFlashAttribute("sucesso", "Vaga publicada com sucesso!");
        } else { // EDIÇÃO
            Anuncio anuncioExistente = anuncioRepository.findById(anuncioForm.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Anúncio inválido para edição:" + anuncioForm.getId()));

            if (!isAnunciante(anuncioExistente, authentication)) {
                redirectAttributes.addFlashAttribute("erro", "Você não tem permissão para editar esta vaga.");
                return "redirect:/home";
            }

            anuncioExistente.setTitulo(anuncioForm.getTitulo());
            anuncioExistente.setDescricao(anuncioForm.getDescricao());
            anuncioExistente.setValor(anuncioForm.getValor());
            anuncioExistente.setLocalizacao(anuncioForm.getLocalizacao());
            anuncioRepository.save(anuncioExistente);
            redirectAttributes.addFlashAttribute("sucesso", "Vaga atualizada com sucesso!");
        }

        return "redirect:/home";
    }

    @GetMapping("/editar/{id}")
    @PreAuthorize("hasAuthority('EMPREGADOR')")
    public String showEditForm(@PathVariable Long id, Model model, Authentication authentication) {
        Anuncio anuncio = anuncioRepository.findById(id).orElseThrow();
        if (!isAnunciante(anuncio, authentication)) {
            return "redirect:/home";
        }
        model.addAttribute("anuncio", anuncio);
        return "anuncio-form";
    }

    @PostMapping("/apagar/{id}")
    @PreAuthorize("hasAuthority('EMPREGADOR')")
    public String apagarAnuncio(@PathVariable Long id, Authentication authentication, RedirectAttributes redirectAttributes) {
        Anuncio anuncio = anuncioRepository.findById(id).orElseThrow();
        if (!isAnunciante(anuncio, authentication)) {
            return "redirect:/home";
        }
        anuncioRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("sucesso", "Vaga apagada com sucesso.");
        return "redirect:/home";
    }

    @PostMapping("/status/{id}")
    @PreAuthorize("hasAuthority('EMPREGADOR')")
    public String alterarStatusAnuncio(@PathVariable Long id, Authentication authentication, RedirectAttributes redirectAttributes) {
        Anuncio anuncio = anuncioRepository.findById(id).orElseThrow();
        if (!isAnunciante(anuncio, authentication)) {
            return "redirect:/home";
        }

        if (anuncio.getStatus() == StatusAnuncio.ATIVO) {
            anuncio.setStatus(StatusAnuncio.PAUSADO);
            redirectAttributes.addFlashAttribute("sucesso", "Vaga pausada.");
        } else if (anuncio.getStatus() == StatusAnuncio.PAUSADO) {
            anuncio.setStatus(StatusAnuncio.ATIVO);
            redirectAttributes.addFlashAttribute("sucesso", "Vaga reativada.");
        }

        anuncioRepository.save(anuncio);
        return "redirect:/home";
    }

    @PostMapping("/arquivar/{id}")
    @PreAuthorize("hasAuthority('EMPREGADOR')")
    public String arquivarAnuncio(@PathVariable Long id, Authentication authentication, RedirectAttributes redirectAttributes) {
        Anuncio anuncio = anuncioRepository.findById(id).orElseThrow();
        if (!isAnunciante(anuncio, authentication)) {
            return "redirect:/home";
        }
        anuncio.setStatus(StatusAnuncio.ARQUIVADO);
        anuncioRepository.save(anuncio);
        redirectAttributes.addFlashAttribute("sucesso", "Vaga arquivada com sucesso.");
        return "redirect:/home";
    }

    @PostMapping("/desarquivar/{id}")
    @PreAuthorize("hasAuthority('EMPREGADOR')")
    public String desarquivarAnuncio(@PathVariable Long id, Authentication authentication, RedirectAttributes redirectAttributes) {
        Anuncio anuncio = anuncioRepository.findById(id).orElseThrow();
        if (!isAnunciante(anuncio, authentication)) {
            return "redirect:/home";
        }
        anuncio.setStatus(StatusAnuncio.PAUSADO);
        anuncioRepository.save(anuncio);
        redirectAttributes.addFlashAttribute("sucesso", "Vaga desarquivada! Ela foi movida para 'Pausadas'.");
        return "redirect:/home";
    }

    @GetMapping("/gerenciar/{id}")
    @PreAuthorize("hasAuthority('EMPREGADOR')")
    public String showGerenciarVaga(@PathVariable Long id, Model model, Authentication authentication, RedirectAttributes redirectAttributes) {
        Anuncio anuncio = anuncioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Anúncio inválido:" + id));

        if (!isAnunciante(anuncio, authentication)) {
            redirectAttributes.addFlashAttribute("erro", "Você não tem permissão para gerenciar esta vaga.");
            return "redirect:/home";
        }

        List<Candidatura> candidaturas = candidaturaRepository.findByAnuncioOrderByDataCandidaturaDesc(anuncio);
        model.addAttribute("anuncio", anuncio);
        model.addAttribute("candidaturas", candidaturas);

        return "empregador-vaga-detalhes";
    }

    // ================= DETALHES ANÚNCIO =================
    @GetMapping("/detalhes/{id}")
    public String showAnuncioDetalhes(@PathVariable Long id, Model model, Authentication authentication, HttpSession session) {
        Anuncio anuncio = anuncioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Anúncio inválido:" + id));

        boolean isAnunciante = false;
        String nomeUsuarioLogado = null;
        boolean jaCandidatado = false;

        if (authentication != null && authentication.isAuthenticated()) {
            String emailUsuarioLogado = authentication.getName();
            Usuario usuarioLogado = usuarioRepository.findByEmail(emailUsuarioLogado).orElse(null);

            if (usuarioLogado != null) {
                if (anuncio.getAnunciante() != null && anuncio.getAnunciante().getId().equals(usuarioLogado.getId())) {
                    isAnunciante = true;
                    nomeUsuarioLogado = usuarioLogado.getNome();
                } else {
                    // Contagem única de views por sessão
                    @SuppressWarnings("unchecked")
                    Set<Long> viewedAnnouncements = (Set<Long>) session.getAttribute("viewedAnnouncements");
                    if (viewedAnnouncements == null) {
                        viewedAnnouncements = new HashSet<>();
                        session.setAttribute("viewedAnnouncements", viewedAnnouncements);
                    }

                    if (!viewedAnnouncements.contains(id)) {
                        anuncio.setVisualizacoes(anuncio.getVisualizacoes() + 1);
                        anuncioRepository.save(anuncio);
                        viewedAnnouncements.add(id);
                        log.debug("View contada para anúncio {} pelo usuário {}", id, emailUsuarioLogado);
                    } else {
                        log.debug("View já contada para anúncio {} nesta sessão", id);
                    }

                    // Verifica se o usuário já se candidatou (independente do tipo)
                    jaCandidatado = candidaturaRepository.existsByColaboradorAndAnuncio(usuarioLogado, anuncio);
                }
            }
        }

        model.addAttribute("anuncio", anuncio);
        model.addAttribute("isAnunciante", isAnunciante);
        model.addAttribute("nomeUsuarioLogado", nomeUsuarioLogado);
        model.addAttribute("jaCandidatado", jaCandidatado);

        return "anuncio-detalhes";
    }
}
