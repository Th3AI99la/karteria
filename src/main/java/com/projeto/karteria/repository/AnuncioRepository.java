package com.projeto.karteria.repository;

import com.projeto.karteria.model.Anuncio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnuncioRepository extends JpaRepository<Anuncio, Long> {
    // Futuramente, podemos adicionar métodos como: findByAnunciante, findByLocalizacao, etc.
}