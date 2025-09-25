package com.projeto.karteria.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.projeto.karteria.model.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    // JpaRepository já nos fornece métodos como:
    // save(), findById(), findAll(), deleteById(), etc.
}