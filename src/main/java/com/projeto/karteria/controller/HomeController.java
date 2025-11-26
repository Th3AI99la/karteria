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
import com.projeto.karteria.model.Notificacao;
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

    Usuario usuarioLogado = getUsuarioLogado(authentication);

    // --- Contagem e lista de notificações não lidas ---
    long contagemNaoLidas = contarNotificacoesNaoLidas(usuarioLogado);
    List<Notificacao> notificacoesNaoLidas = buscarNotificacoesNaoLidas(usuarioLogado);

    model.addAttribute("contagemNotificacoesNaoLidas", contagemNaoLidas);
    model.addAttribute("notificacoesNaoLidas", notificacoesNaoLidas);

    if (perfilAtivo == TipoUsuario.EMPREGADOR) {
      prepararHomeEmpregador(model, usuarioLogado);
      return "area-empregador";
    } else {
      prepararHomeColaborador(model);
      return "area-colaborador";
    }
  }

  // ===================== MÉTODOS AUXILIARES =====================

  private Usuario getUsuarioLogado(Authentication authentication) {
    String email = authentication.getName();
    return usuarioRepository
        .findByEmail(email)
        .orElseThrow(() -> new RuntimeException("Usuário não encontrado: " + email));
  }

  private long contarNotificacoesNaoLidas(Usuario usuario) {
    return notificacaoRepository.countByUsuarioDestinatarioAndLidaIsFalse(usuario);
  }

  private List<Notificacao> buscarNotificacoesNaoLidas(Usuario usuario) {
    return notificacaoRepository.findByUsuarioDestinatarioAndLidaIsFalseOrderByDataCriacaoDesc(
        usuario);
  }

  private void prepararHomeEmpregador(Model model, Usuario usuarioLogado) {
    List<Anuncio> todosAnuncios = anuncioRepository.findByAnuncianteOrderByDataPostagemDesc(usuarioLogado);

    Map<StatusAnuncio, List<Anuncio>> anunciosPorStatus = todosAnuncios.stream()
        .collect(Collectors.groupingBy(Anuncio::getStatus));

    List<Anuncio> vagasAtivas = anunciosPorStatus.getOrDefault(StatusAnuncio.ATIVO, new ArrayList<>());
    List<Anuncio> vagasPausadas = anunciosPorStatus.getOrDefault(StatusAnuncio.PAUSADO, new ArrayList<>());
    List<Anuncio> vagasArquivadas = anunciosPorStatus.getOrDefault(StatusAnuncio.ARQUIVADO, new ArrayList<>());

    List<Anuncio> vagasConcluidas = anunciosPorStatus.getOrDefault(StatusAnuncio.CONCLUIDO, new ArrayList<>());

    List<Anuncio> todasAsVagas = new ArrayList<>();
    todasAsVagas.addAll(vagasAtivas);
    todasAsVagas.addAll(vagasPausadas);
    todasAsVagas.addAll(vagasArquivadas);
    todasAsVagas.addAll(vagasConcluidas); 

    model.addAttribute("vagasAtivas", vagasAtivas);
    model.addAttribute("vagasPausadas", vagasPausadas);
    model.addAttribute("vagasArquivadas", vagasArquivadas);
    model.addAttribute("vagasConcluidas", vagasConcluidas); 
    model.addAttribute("todasAsVagas", todasAsVagas);
  }

  private void prepararHomeColaborador(Model model) {
    List<Anuncio> anunciosAtivos = anuncioRepository.findByStatusOrderByDataPostagemDesc(StatusAnuncio.ATIVO);
    model.addAttribute("anuncios", anunciosAtivos);
    model.addAttribute("totalAnunciosAtivos", anunciosAtivos.size());
  }
}
