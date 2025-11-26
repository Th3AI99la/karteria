package com.projeto.karteria.model;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column; 
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "anuncios")
public class Anuncio {

  // --- Chave Primária ---
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // --- Campos de Dados ---
  private String titulo;

  @Column(length = 2000) 
  private String descricao;

  private String localizacao;
  private LocalDateTime dataPostagem;

  private Double valorMin;
  private Double valorMax;
  private String tipoPagamento;

  private boolean exibirTelefone = false;
  private boolean permitirContato = true;

  @Enumerated(EnumType.STRING)
  private StatusAnuncio status;

  @OneToOne(mappedBy = "anuncio", cascade = CascadeType.ALL)
  private Avaliacao avaliacao;

  // --- Relacionamentos ---
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "anunciante_id")
  private Usuario anunciante;

  @OneToMany(mappedBy = "anuncio", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Candidatura> candidaturas;

  private int visualizacoes = 0; 

  // --- Getters e Setters ---
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getTitulo() {
    return titulo;
  }

  public void setTitulo(String titulo) {
    this.titulo = titulo;
  }

  public String getDescricao() {
    return descricao;
  }

  public void setDescricao(String descricao) {
    this.descricao = descricao;
  }

  public String getLocalizacao() {
    return localizacao;
  }

  public void setLocalizacao(String localizacao) {
    this.localizacao = localizacao;
  }

  public LocalDateTime getDataPostagem() {
    return dataPostagem;
  }

  public void setDataPostagem(LocalDateTime dataPostagem) {
    this.dataPostagem = dataPostagem;
  }

  public StatusAnuncio getStatus() {
    return status;
  }

  public void setStatus(StatusAnuncio status) {
    this.status = status;
  }

  public Usuario getAnunciante() {
    return anunciante;
  }

  public void setAnunciante(Usuario anunciante) {
    this.anunciante = anunciante;
  }

  public List<Candidatura> getCandidaturas() {
    return candidaturas;
  }

  public Avaliacao getAvaliacao() {
      return avaliacao;
  }

  public void setAvaliacao(Avaliacao avaliacao) {
      this.avaliacao = avaliacao;
  }

  public void setCandidaturas(List<Candidatura> candidaturas) {
    this.candidaturas = candidaturas;
  }

  public int getVisualizacoes() {
    return visualizacoes;
  }

  public void setVisualizacoes(int visualizacoes) {
    this.visualizacoes = visualizacoes;
  }

  public Double getValorMin() {
    return valorMin;
  }

  public void setValorMin(Double valorMin) {
    this.valorMin = valorMin;
  }

  public Double getValorMax() {
    return valorMax;
  }

  public void setValorMax(Double valorMax) {
    this.valorMax = valorMax;
  }

  public String getTipoPagamento() {
    return tipoPagamento;
  }

  public void setTipoPagamento(String tipoPagamento) {
    this.tipoPagamento = tipoPagamento;
  }

  public boolean isExibirTelefone() {
    return exibirTelefone;
  }

  public void setExibirTelefone(boolean exibirTelefone) {
    this.exibirTelefone = exibirTelefone;
  }

  public boolean isPermitirContato() {
    return permitirContato;
  }

  public void setPermitirContato(boolean permitirContato) {
    this.permitirContato = permitirContato;
  }
}