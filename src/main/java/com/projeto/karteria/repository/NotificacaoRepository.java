package com.projeto.karteria.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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

  // Encontra notificações LIDAS para um usuário específico
  List<Notificacao> findByUsuarioDestinatarioAndLidaIsTrueOrderByDataCriacaoDesc(Usuario usuario);

  // Marca todas as notificações como lidas para um usuário específico
  @Transactional
    @Modifying
    @Query("UPDATE Notificacao n SET n.lida = true WHERE n.usuarioDestinatario = :usuario AND n.lida = false")
    void marcarTodasComoLidas(@Param("usuario") Usuario usuario);

}
