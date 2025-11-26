package com.projeto.karteria.model;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority; // Importação adicionada
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "usuarios")
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    @Column(unique = true, nullable = false)
    private String email;

    private String senha;
    private String sobrenome;
    private String telefone;
    private String telefone2;
    private String cpf;
    private String endereco;

    private boolean cadastroCompleto = false;

    @Enumerated(EnumType.STRING)
    private TipoUsuario tipo;

    @OneToMany(mappedBy = "anunciante", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<Anuncio> anuncios;

    // --- NOVO CAMPO ADICIONADO ---
    // Relacionamento com as avaliações onde este usuário é o "avaliado"
    @OneToMany(mappedBy = "avaliado", fetch = FetchType.LAZY)
    private List<Avaliacao> avaliacoesRecebidas;
    // -----------------------------

    private String resetToken;
    private LocalDateTime resetTokenExpiry;

    @Column(nullable = true, updatable = false)
    private LocalDateTime dataCadastro;

    // --- Construtores ---
    public Usuario() {
    }

    public Usuario(String nome, String email, String senha, TipoUsuario tipo) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.tipo = tipo;
    }

    // --- Getters e Setters ---
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

    public String getSobrenome() {
        return sobrenome;
    }

    public void setSobrenome(String sobrenome) {
        this.sobrenome = sobrenome;
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

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getTelefone2() {
        return telefone2;
    }

    public void setTelefone2(String telefone2) {
        this.telefone2 = telefone2;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public boolean isCadastroCompleto() {
        return cadastroCompleto;
    }

    public void setCadastroCompleto(boolean cadastroCompleto) {
        this.cadastroCompleto = cadastroCompleto;
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

    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(LocalDateTime dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    // --- NOVOS MÉTODOS PARA AVALIAÇÃO ---

    public List<Avaliacao> getAvaliacoesRecebidas() {
        return avaliacoesRecebidas;
    }

    public void setAvaliacoesRecebidas(List<Avaliacao> avaliacoesRecebidas) {
        this.avaliacoesRecebidas = avaliacoesRecebidas;
    }

    // Calcula a média das notas recebidas (útil para exibir no perfil)
    public Double getMediaAvaliacoes() {
        if (avaliacoesRecebidas == null || avaliacoesRecebidas.isEmpty()) {
            return 0.0;
        }
        // Assume que Avaliacao tem um método getNota() que retorna um número
        double soma = avaliacoesRecebidas.stream().mapToInt(Avaliacao::getNota).sum();
        return soma / avaliacoesRecebidas.size();
    }

    // Retorna o total de avaliações recebidas
    public int getTotalAvaliacoes() {
        return (avaliacoesRecebidas == null) ? 0 : avaliacoesRecebidas.size();
    }
    // ------------------------------------

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

    // --- Métodos auxiliares ---
    public boolean isResetTokenValid() {
        return resetToken != null
                && resetTokenExpiry != null
                && LocalDateTime.now().isBefore(resetTokenExpiry);
    }

    // --- Métodos do UserDetails ---
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (this.tipo == null)
            return Collections.emptyList();
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

    // --- equals, hashCode e toString ---
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Usuario))
            return false;
        Usuario other = (Usuario) o;
        return id != null && id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Usuario{"
                + "id="
                + id
                + ", email='"
                + email
                + '\''
                + ", tipo="
                + tipo
                + ", cadastroCompleto="
                + cadastroCompleto
                + '}';
    }
}