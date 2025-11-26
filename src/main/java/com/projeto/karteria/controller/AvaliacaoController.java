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
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        Usuario avaliador = usuarioRepository.findByEmail(authentication.getName()).orElseThrow();
        Usuario avaliado = usuarioRepository.findById(candidatoId).orElseThrow();
        Anuncio anuncio = anuncioRepository.findById(anuncioId).orElseThrow();

        // Cria a avaliação
        Avaliacao avaliacao = new Avaliacao();
        avaliacao.setAvaliador(avaliador);
        avaliacao.setAvaliado(avaliado);
        avaliacao.setAnuncio(anuncio);
        avaliacao.setNota(nota);
        avaliacao.setComentario(comentario);

        avaliacaoRepository.save(avaliacao);

        //Atualiza o status do anúncio para CONCLUIDO
        anuncio.setStatus(StatusAnuncio.CONCLUIDO);
        anuncioRepository.save(anuncio);

        redirectAttributes.addFlashAttribute("sucesso", "Avaliação enviada e serviço concluído!");
        
        return "redirect:/anuncios/gerenciar/" + anuncioId;
    }
}
