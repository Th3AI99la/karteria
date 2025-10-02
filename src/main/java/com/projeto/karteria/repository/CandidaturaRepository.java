package com.projeto.karteria.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.projeto.karteria.model.Candidatura;

@Repository
public interface CandidaturaRepository extends JpaRepository<Candidatura, Long> {
}