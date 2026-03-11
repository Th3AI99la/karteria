package com.projeto.karteria.dto;

import java.time.LocalDateTime;

public class AnuncioResponseDTO {
    private Long id;
    private String titulo;
    private String descricao;
    private String localizacao;
    private Double valorMin;
    private Double valorMax;
    private String tipoPagamento;
    private String nomeAnunciante;
    private LocalDateTime dataPostagem;

    public AnuncioResponseDTO(Long id, String titulo, String descricao, String localizacao,
            Double valorMin, Double valorMax, String tipoPagamento,
            String nomeAnunciante, LocalDateTime dataPostagem) {
        this.id = id;
        this.titulo = titulo;
        this.descricao = descricao;
        this.localizacao = localizacao;
        this.valorMin = valorMin;
        this.valorMax = valorMax;
        this.tipoPagamento = tipoPagamento;
        this.nomeAnunciante = nomeAnunciante;
        this.dataPostagem = dataPostagem;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getLocalizacao() {
        return localizacao;
    }

    public Double getValorMin() {
        return valorMin;
    }

    public Double getValorMax() {
        return valorMax;
    }

    public String getTipoPagamento() {
        return tipoPagamento;
    }

    public String getNomeAnunciante() {
        return nomeAnunciante;
    }

    public LocalDateTime getDataPostagem() {
        return dataPostagem;
    }
}