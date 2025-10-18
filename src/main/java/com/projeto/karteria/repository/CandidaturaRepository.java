package com.projeto.karteria.repository;

import java.util.List; 

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.projeto.karteria.model.Anuncio;
import com.projeto.karteria.model.Candidatura;

@Repository
public interface CandidaturaRepository extends JpaRepository<Candidatura, Long> {

    // Listar candidaturas por anúncio, ordenadas pela data de candidatura em ordem decrescente
    List<Candidatura> findByAnuncioOrderByDataCandidaturaDesc(Anuncio anuncio);

}