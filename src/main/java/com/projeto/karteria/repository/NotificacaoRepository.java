package com.projeto.karteria.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.projeto.karteria.model.Notificacao;
import com.projeto.karteria.model.Usuario;

@Repository
public interface NotificacaoRepository extends JpaRepository<Notificacao, Long> {

    // Encontra notificações para um usuário específico, ordenadas pela mais recente
    List<Notificacao> findByUsuarioDestinatarioOrderByDataCriacaoDesc(Usuario usuario);

    // Encontra notificações NÃO LIDAS para um usuário específico
    List<Notificacao> findByUsuarioDestinatarioAndLidaIsFalseOrderByDataCriacaoDesc(Usuario usuario);

    // Conta notificações NÃO LIDAS para um usuário específico
    long countByUsuarioDestinatarioAndLidaIsFalse(Usuario usuario);
}