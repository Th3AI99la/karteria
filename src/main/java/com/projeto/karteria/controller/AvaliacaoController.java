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

    @Autowired
    private AvaliacaoRepository avaliacaoRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private AnuncioRepository anuncioRepository;

    @PostMapping("/avaliar/salvar")
    public String salvarAvaliacao(
            @RequestParam Long anuncioId,
            @RequestParam Long candidatoId,
            @RequestParam Integer nota,
            @RequestParam String comentario,
            @RequestParam String codigoValidacaoInput, // <--- Recebe o código do HTML
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        Usuario avaliador = usuarioRepository.findByEmail(authentication.getName()).orElseThrow();
        Usuario avaliado = usuarioRepository.findById(candidatoId).orElseThrow();
        Anuncio anuncio = anuncioRepository.findById(anuncioId).orElseThrow();

        // 4. LÓGICA DE VALIDAÇÃO: Verifica se o código bate
        String codigoCorreto = avaliado.getCodigoValidacao();

        if (codigoCorreto == null || !codigoCorreto.equalsIgnoreCase(codigoValidacaoInput.trim())) {
            // --- MENSAGEM DE ERRO ---
            redirectAttributes.addFlashAttribute("erro",
                    "Código incorreto! Solicite ao colaborador o código de Validação para concluir.");
            return "redirect:/anuncios/gerenciar/" + anuncioId;
        }

        // Se passou, salva tudo
        Avaliacao avaliacao = new Avaliacao();
        avaliacao.setAvaliador(avaliador);
        avaliacao.setAvaliado(avaliado);
        avaliacao.setAnuncio(anuncio);
        avaliacao.setNota(nota);
        avaliacao.setComentario(comentario);

        avaliacaoRepository.save(avaliacao);

        anuncio.setStatus(StatusAnuncio.CONCLUIDO);
        anuncioRepository.save(anuncio);

        redirectAttributes.addFlashAttribute("sucesso", "Serviço validado e avaliação registrada com sucesso!");

        return "redirect:/anuncios/gerenciar/" + anuncioId;
    }
}