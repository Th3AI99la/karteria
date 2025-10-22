package com.projeto.karteria.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Table(name = "usuarios")
public class Usuario implements UserDetails {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String nome;
  private String email;
  private String senha;

  @Enumerated(EnumType.STRING)
  private TipoUsuario tipo;

  @OneToMany(mappedBy = "anunciante", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Anuncio> anuncios;

  private String resetToken;
  private LocalDateTime resetTokenExpiry;

  // --- Getters e Setters Manuais ---
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getNome() {
    return nome;
  }

  public void setNome(String nome) {
    this.nome = nome;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getSenha() {
    return senha;
  }

  public void setSenha(String senha) {
    this.senha = senha;
  }

  public TipoUsuario getTipo() {
    return tipo;
  }

  public void setTipo(TipoUsuario tipo) {
    this.tipo = tipo;
  }

  public List<Anuncio> getAnuncios() {
    return anuncios;
  }

  public void setAnuncios(List<Anuncio> anuncios) {
    this.anuncios = anuncios;
  }

  public String getResetToken() {
    return resetToken;
  }

  public void setResetToken(String resetToken) {
    this.resetToken = resetToken;
  }

  public LocalDateTime getResetTokenExpiry() {
    return resetTokenExpiry;
  }

  public void setResetTokenExpiry(LocalDateTime resetTokenExpiry) {
    this.resetTokenExpiry = resetTokenExpiry;
  }

  // --- UserDetails ---
  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    if (this.tipo == null) return Collections.emptyList();
    return Collections.singletonList(new SimpleGrantedAuthority(this.tipo.name()));
  }

  @Override
  public String getPassword() {
    return this.senha;
  }

  @Override
  public String getUsername() {
    return this.email;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}
