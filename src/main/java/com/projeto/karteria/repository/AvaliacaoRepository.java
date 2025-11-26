package com.projeto.karteria.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.projeto.karteria.model.Avaliacao;
import com.projeto.karteria.model.Usuario;

@Repository
public interface AvaliacaoRepository extends JpaRepository<Avaliacao, Long> {
    List<Avaliacao> findByAvaliado(Usuario avaliado);
}