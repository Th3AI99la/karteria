package com.projeto.karteria.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.projeto.karteria.model.Anuncio;
import com.projeto.karteria.model.StatusAnuncio;
import com.projeto.karteria.model.TipoUsuario;
import com.projeto.karteria.model.Usuario;
import com.projeto.karteria.repository.AnuncioRepository;
import com.projeto.karteria.repository.NotificacaoRepository;
import com.projeto.karteria.repository.UsuarioRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private AnuncioRepository anuncioRepository;
    @Autowired
    private NotificacaoRepository notificacaoRepository;

    @GetMapping("/")
    public String showIndexPage() {
        return "index";
    }

    @GetMapping("/home")
    public String showHomePage(HttpSession session, Model model, Authentication authentication) {
        TipoUsuario perfilAtivo = (TipoUsuario) session.getAttribute("perfilAtivo");

        if (perfilAtivo == null) {
            return "redirect:/escolher-perfil";
        }

        String email = authentication.getName();
        Usuario usuarioLogado = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado: " + email));

        // --- LÓGICA DO EMPREGADOR ---
        if (perfilAtivo == TipoUsuario.EMPREGADOR) {

            List<Anuncio> todosAnunciosDoUsuario = anuncioRepository
                    .findByAnuncianteOrderByDataPostagemDesc(usuarioLogado);

            // Agrupa por status para evitar múltiplos streams
            Map<StatusAnuncio, List<Anuncio>> anunciosPorStatus = todosAnunciosDoUsuario.stream()
                    .collect(Collectors.groupingBy(Anuncio::getStatus));

            List<Anuncio> vagasAtivas = anunciosPorStatus.getOrDefault(StatusAnuncio.ATIVO, new ArrayList<>());
            List<Anuncio> vagasPausadas = anunciosPorStatus.getOrDefault(StatusAnuncio.PAUSADO, new ArrayList<>());
            List<Anuncio> vagasArquivadas = anunciosPorStatus.getOrDefault(StatusAnuncio.ARQUIVADO, new ArrayList<>());

            model.addAttribute("vagasAtivas", vagasAtivas);
            model.addAttribute("vagasPausadas", vagasPausadas);
            model.addAttribute("vagasArquivadas", vagasArquivadas);

            // Combina todas as vagas em uma lista para os modais
            List<Anuncio> todasAsVagas = new ArrayList<>();
            todasAsVagas.addAll(vagasAtivas);
            todasAsVagas.addAll(vagasPausadas);
            todasAsVagas.addAll(vagasArquivadas);
            model.addAttribute("todasAsVagas", todasAsVagas);

            // Contagem de notificações não lidas ---
            long contagemNaoLidas = notificacaoRepository
                    .countByUsuarioDestinatarioAndLidaIsFalse(usuarioLogado);
            model.addAttribute("contagemNotificacoesNaoLidas", contagemNaoLidas);

            return "area-empregador";
        }

        // --- LÓGICA DO COLABORADOR ---
        else {
            List<Anuncio> anunciosAtivos = anuncioRepository.findByStatusOrderByDataPostagemDesc(StatusAnuncio.ATIVO);

            model.addAttribute("anuncios", anunciosAtivos);
            model.addAttribute("totalAnunciosAtivos", anunciosAtivos.size());

            // Contagem de notificações não lidas para colaborador ---
            long contagemNaoLidasColab = notificacaoRepository
                    .countByUsuarioDestinatarioAndLidaIsFalse(usuarioLogado);
            model.addAttribute("contagemNotificacoesNaoLidas", contagemNaoLidasColab);


            return "area-colaborador";
        }
    }

}
