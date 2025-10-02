package com.projeto.karteria.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository; // Importar Usuario
import org.springframework.stereotype.Repository;

import com.projeto.karteria.model.Anuncio;
import com.projeto.karteria.model.Usuario; // Importar List

@Repository
public interface AnuncioRepository extends JpaRepository<Anuncio, Long> {
    // O Spring Data JPA cria a query automaticamente: "encontre todos os Anuncios pelo objeto anunciante"
    List<Anuncio> findByAnuncianteOrderByDataPostagemDesc(Usuario anunciante);
}