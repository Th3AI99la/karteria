package com.projeto.karteria.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.projeto.karteria.model.Anuncio;
import com.projeto.karteria.model.StatusAnuncio;
import com.projeto.karteria.model.Usuario;

@Repository
public interface AnuncioRepository extends JpaRepository<Anuncio, Long> {
    // Método existente para empregador
    List<Anuncio> findByAnuncianteOrderByDataPostagemDesc(Usuario anunciante);

    // Método: Busca anúncios por status, ordenados por data
    List<Anuncio> findByStatusOrderByDataPostagemDesc(StatusAnuncio status);
}