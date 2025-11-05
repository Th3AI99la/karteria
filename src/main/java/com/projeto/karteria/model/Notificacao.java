package com.projeto.karteria.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "notificacoes")
public class Notificacao {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // Usuário que RECEBERÁ a notificação (neste caso, o Empregador)
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "usuario_destinatario_id")
  private Usuario usuarioDestinatario;

  private String mensagem;
  private LocalDateTime dataCriacao;
  private boolean lida = false;

  // Link relacionado à notificação (pode ser uma URL ou um identificador de
  // recurso)
  private String link;

  // --- Construtores ---
  public Notificacao() {}

  public Notificacao(Usuario usuarioDestinatario, String mensagem, String link) {
    this.usuarioDestinatario = usuarioDestinatario;
    this.mensagem = mensagem;
    this.link = link;
    this.dataCriacao = LocalDateTime.now();
    this.lida = false;
  }

  // --- Getters e Setters ---
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Usuario getUsuarioDestinatario() {
    return usuarioDestinatario;
  }

  public void setUsuarioDestinatario(Usuario usuarioDestinatario) {
    this.usuarioDestinatario = usuarioDestinatario;
  }

  public String getMensagem() {
    return mensagem;
  }

  public void setMensagem(String mensagem) {
    this.mensagem = mensagem;
  }

  public LocalDateTime getDataCriacao() {
    return dataCriacao;
  }

  public void setDataCriacao(LocalDateTime dataCriacao) {
    this.dataCriacao = dataCriacao;
  }

  public boolean isLida() {
    return lida;
  }

  public void setLida(boolean lida) {
    this.lida = lida;
  }

  public String getLink() {
    return link;
  }

  public void setLink(String link) {
    this.link = link;
  }
}
