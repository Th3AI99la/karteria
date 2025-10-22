package com.projeto.karteria.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.projeto.karteria.model.Anuncio;
import com.projeto.karteria.model.Candidatura;
import com.projeto.karteria.model.Usuario;

@Repository
public interface CandidaturaRepository extends JpaRepository<Candidatura, Long> {

  // Listar candidaturas por anúncio, ordenadas pela data de candidatura em ordem decrescente
  List<Candidatura> findByAnuncioOrderByDataCandidaturaDesc(Anuncio anuncio);

  // Verificar se uma candidatura já existe para um colaborador e anúncio específicos  
  boolean existsByColaboradorAndAnuncio(Usuario colaborador, Anuncio anuncio);
}
