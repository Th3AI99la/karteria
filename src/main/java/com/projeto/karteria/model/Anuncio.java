package com.projeto.karteria.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "anuncios")
@Getter
@Setter
public class Anuncio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String titulo;
    private String descricao;
    private BigDecimal valor;
    private String localizacao;
    private LocalDateTime dataPostagem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "anunciante_id")
    private Usuario anunciante;
}