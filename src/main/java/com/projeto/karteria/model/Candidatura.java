package com.projeto.karteria.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "candidaturas")
public class Candidatura {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "colaborador_id")
  private Usuario colaborador;

  @ManyToOne
  @JoinColumn(name = "anuncio_id")
  private Anuncio anuncio;

  private LocalDateTime dataCandidatura;

  // --- Getters e Setters Manuais ---

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Usuario getColaborador() {
    return colaborador;
  }

  public void setColaborador(Usuario colaborador) {
    this.colaborador = colaborador;
  }

  public Anuncio getAnuncio() {
    return anuncio;
  }

  public void setAnuncio(Anuncio anuncio) {
    this.anuncio = anuncio;
  }

  public LocalDateTime getDataCandidatura() {
    return dataCandidatura;
  }

  public void setDataCandidatura(LocalDateTime dataCandidatura) {
    this.dataCandidatura = dataCandidatura;
  }
}
