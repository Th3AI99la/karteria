package com.projeto.karteria.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.projeto.karteria.model.Anuncio;
import com.projeto.karteria.model.Avaliacao;
import com.projeto.karteria.model.StatusAnuncio;
import com.projeto.karteria.model.Usuario;
import com.projeto.karteria.repository.AnuncioRepository;
import com.projeto.karteria.repository.AvaliacaoRepository;
import com.projeto.karteria.repository.UsuarioRepository;

@Controller
public class AvaliacaoController {

    @Autowired private AvaliacaoRepository avaliacaoRepository;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private AnuncioRepository anuncioRepository;

    @PostMapping("/avaliar/salvar")
    public String salvarAvaliacao(
            @RequestParam Long anuncioId,
            @RequestParam Long candidatoId,
            @RequestParam Integer nota,
            @RequestParam String comentario,
            @RequestParam String codigoValidacaoInput, // <--- NOVO CAMPO
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        Usuario avaliador = usuarioRepository.findByEmail(authentication.getName()).orElseThrow();
        Usuario avaliado = usuarioRepository.findById(candidatoId).orElseThrow();
        Anuncio anuncio = anuncioRepository.findById(anuncioId).orElseThrow();

        // 1. SEGURANÇA: Verifica se o código confere
        if (avaliado.getCodigoValidacao() == null || 
            !avaliado.getCodigoValidacao().equalsIgnoreCase(codigoValidacaoInput.trim())) {
            
            redirectAttributes.addFlashAttribute("erro", "Código de validação incorreto! A avaliação não foi registrada.");
            return "redirect:/anuncios/gerenciar/" + anuncioId;
        }

        // 2. Cria a avaliação
        Avaliacao avaliacao = new Avaliacao();
        avaliacao.setAvaliador(avaliador);
        avaliacao.setAvaliado(avaliado);
        avaliacao.setAnuncio(anuncio);
        avaliacao.setNota(nota);
        avaliacao.setComentario(comentario);

        avaliacaoRepository.save(avaliacao);

        // 3. Atualiza status e salva
        anuncio.setStatus(StatusAnuncio.CONCLUIDO);
        anuncioRepository.save(anuncio);

        redirectAttributes.addFlashAttribute("sucesso", "Serviço validado e avaliação registrada com sucesso!");
        
        return "redirect:/anuncios/gerenciar/" + anuncioId;
    }
}