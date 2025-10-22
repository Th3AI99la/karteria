package com.projeto.karteria.controller;

import java.time.LocalDateTime;
import java.util.List;

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

@Controller
@RequestMapping("/anuncios")
public class AnuncioController {

    @Autowired
    private AnuncioRepository anuncioRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private CandidaturaRepository candidaturaRepository;

    // MÉTODO PARA MOSTRAR FORMULÁRIO DE CRIAÇÃO
    @GetMapping("/novo")
    @PreAuthorize("hasAuthority('EMPREGADOR')")
    public String showAnuncioForm(Model model) {
        model.addAttribute("anuncio", new Anuncio());
        return "anuncio-form";
    }

    // MÉTODO PARA SALVAR ANÚNCIO
    @PostMapping("/salvar")
    @PreAuthorize("hasAuthority('EMPREGADOR')")
    public String salvarAnuncio(
            @ModelAttribute Anuncio anuncioForm, // Renomeado para clareza
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        String email = authentication.getName();
        Usuario usuarioLogado = usuarioRepository.findByEmail(email).orElseThrow();

        // --- SE FOR UMA NOVA VAGA ---
        if (anuncioForm.getId() == null) {
            anuncioForm.setAnunciante(usuarioLogado);
            anuncioForm.setDataPostagem(LocalDateTime.now());
            anuncioForm.setStatus(StatusAnuncio.ATIVO);
            anuncioRepository.save(anuncioForm); // Salva o novo anúncio diretamente
            redirectAttributes.addFlashAttribute("sucesso", "Vaga publicada com sucesso!");
        }
        // --- SE FOR UMA EDIÇÃO ---
        else {
            // 1. Busca o anúncio EXISTENTE no banco
            Anuncio anuncioExistente = anuncioRepository
                    .findById(anuncioForm.getId())
                    .orElseThrow(
                            () -> new IllegalArgumentException(
                                    "Anúncio inválido para edição:" + anuncioForm.getId()));

            // 2. Validação de segurança: o usuário logado é o dono?
            if (!anuncioExistente.getAnunciante().getEmail().equals(authentication.getName())) {
                redirectAttributes.addFlashAttribute(
                        "erro", "Você não tem permissão para editar esta vaga.");
                return "redirect:/home";
            }

            // 3. ATUALIZA os campos do anúncio EXISTENTE com os dados do FORMULÁRIO
            anuncioExistente.setTitulo(anuncioForm.getTitulo());
            anuncioExistente.setDescricao(anuncioForm.getDescricao());
            anuncioExistente.setValor(anuncioForm.getValor());
            anuncioExistente.setLocalizacao(anuncioForm.getLocalizacao());
            // Mantemos o anunciante, dataPostagem e status originais (não são editáveis no
            // form)

            // 4. SALVA o anúncio EXISTENTE (que contém a referência correta da coleção
            // 'candidaturas')
            anuncioRepository.save(anuncioExistente); // <-- SALVA O OBJETO CORRETO
            redirectAttributes.addFlashAttribute("sucesso", "Vaga atualizada com sucesso!");
        }

        return "redirect:/home";
    }

    // MÉTODO PARA MOSTRAR FORMULÁRIO DE EDIÇÃO
    @GetMapping("/editar/{id}")
    @PreAuthorize("hasAuthority('EMPREGADOR')")
    public String showEditForm(@PathVariable Long id, Model model, Authentication authentication) {
        Anuncio anuncio = anuncioRepository.findById(id).orElseThrow();
        if (!anuncio.getAnunciante().getEmail().equals(authentication.getName())) {
            return "redirect:/home";
        }
        model.addAttribute("anuncio", anuncio);
        return "anuncio-form";
    }

    // MÉTODO PARA APAGAR ANÚNCIO
    @PostMapping("/apagar/{id}")
    @PreAuthorize("hasAuthority('EMPREGADOR')")
    public String apagarAnuncio(
            @PathVariable Long id, Authentication authentication, RedirectAttributes redirectAttributes) {
        Anuncio anuncio = anuncioRepository.findById(id).orElseThrow();
        if (!anuncio.getAnunciante().getEmail().equals(authentication.getName())) {
            return "redirect:/home";
        }
        anuncioRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("sucesso", "Vaga apagada com sucesso.");
        return "redirect:/home";
    }

    // MÉTODO PARA ALTERAR STATUS ENTRE ATIVO E PAUSADO
    @PostMapping("/status/{id}")
    @PreAuthorize("hasAuthority('EMPREGADOR')")
    public String alterarStatusAnuncio(
            @PathVariable Long id, Authentication authentication, RedirectAttributes redirectAttributes) {
        Anuncio anuncio = anuncioRepository.findById(id).orElseThrow();
        if (!anuncio.getAnunciante().getEmail().equals(authentication.getName())) {
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

    // MÉTODO PARA ARQUIVAR
    @PostMapping("/arquivar/{id}")
    @PreAuthorize("hasAuthority('EMPREGADOR')")
    public String arquivarAnuncio(
            @PathVariable Long id, Authentication authentication, RedirectAttributes redirectAttributes) {
        Anuncio anuncio = anuncioRepository.findById(id).orElseThrow();

        if (!anuncio.getAnunciante().getEmail().equals(authentication.getName())) {
            return "redirect:/home";
        }

        anuncio.setStatus(StatusAnuncio.ARQUIVADO);
        anuncioRepository.save(anuncio);

        redirectAttributes.addFlashAttribute("sucesso", "Vaga arquivada com sucesso.");
        return "redirect:/home";
    }

    // MÉTODO PARA DESARQUIVAR
    @PostMapping("/desarquivar/{id}")
    @PreAuthorize("hasAuthority('EMPREGADOR')")
    public String desarquivarAnuncio(
            @PathVariable Long id, Authentication authentication, RedirectAttributes redirectAttributes) {
        Anuncio anuncio = anuncioRepository.findById(id).orElseThrow();

        if (!anuncio.getAnunciante().getEmail().equals(authentication.getName())) {
            return "redirect:/home";
        }

        anuncio.setStatus(StatusAnuncio.PAUSADO);
        anuncioRepository.save(anuncio);

        redirectAttributes.addFlashAttribute(
                "sucesso", "Vaga desarquivada! Ela foi movida para 'Pausadas'.");
        return "redirect:/home";
    }

    // MÉTODO PARA GERENCIAR VAGA (VER CANDIDATOS)
    @GetMapping("/gerenciar/{id}")
    @PreAuthorize("hasAuthority('EMPREGADOR')")
    public String showGerenciarVaga(
            @PathVariable Long id,
            Model model,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        // 1. Busca o anúncio ou lança exceção
        Anuncio anuncio = anuncioRepository
                .findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Anúncio inválido:" + id));

        // 2. Validação de segurança: o usuário logado é o dono do anúncio?
        if (!anuncio.getAnunciante().getEmail().equals(authentication.getName())) {
            redirectAttributes.addFlashAttribute(
                    "erro", "Você não tem permissão para gerenciar esta vaga.");
            return "redirect:/home"; // Redireciona se não for o dono
        }

        // 3. Busca a lista de candidaturas para este anúncio
        List<Candidatura> candidaturas = candidaturaRepository.findByAnuncioOrderByDataCandidaturaDesc(anuncio);

        // 4. Adiciona o anúncio e a lista de candidaturas ao modelo
        model.addAttribute("anuncio", anuncio);
        model.addAttribute("candidaturas", candidaturas);

        // 5. Retorna o nome da nova view que vamos criar
        return "empregador-vaga-detalhes";
    }

    // MÉTODO PARA EXIBIR DETALHES DA VAGA (Colaborador)
    @GetMapping("/detalhes/{id}")
    public String showAnuncioDetalhes(@PathVariable Long id, Model model, Authentication authentication) {
        Anuncio anuncio = anuncioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Anúncio inválido:" + id));

        // ** INCREMENTA VISUALIZAÇÕES (se não for o dono) **
        if (authentication != null && !anuncio.getAnunciante().getEmail().equals(authentication.getName())) {
            anuncio.setVisualizacoes(anuncio.getVisualizacoes() + 1);
            anuncioRepository.save(anuncio); // Salva o incremento
        }
        // *************************************************

        model.addAttribute("anuncio", anuncio);
        return "anuncio-detalhes";
    }
}
