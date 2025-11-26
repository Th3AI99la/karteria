package com.projeto.karteria.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "avaliacoes")
public class Avaliacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "avaliador_id")
    private Usuario avaliador; // Quem dá a nota (Empregador)

    @ManyToOne
    @JoinColumn(name = "avaliado_id")
    private Usuario avaliado; // Quem recebe a nota (Colaborador)

    @ManyToOne
    @JoinColumn(name = "anuncio_id")
    private Anuncio anuncio; // Qual serviço foi realizado

    private Integer nota; // De 1 a 5
    private String comentario;
    private LocalDateTime dataAvaliacao;

    // Construtor padrão e com argumentos
    public Avaliacao() {
        this.dataAvaliacao = LocalDateTime.now();
    }

    // Getters e Setters (gere-os na sua IDE ou use Lombok se estiver configurado)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Usuario getAvaliador() { return avaliador; }
    public void setAvaliador(Usuario avaliador) { this.avaliador = avaliador; }
    public Usuario getAvaliado() { return avaliado; }
    public void setAvaliado(Usuario avaliado) { this.avaliado = avaliado; }
    public Anuncio getAnuncio() { return anuncio; }
    public void setAnuncio(Anuncio anuncio) { this.anuncio = anuncio; }
    public Integer getNota() { return nota; }
    public void setNota(Integer nota) { this.nota = nota; }
    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }
    public LocalDateTime getDataAvaliacao() { return dataAvaliacao; }
}