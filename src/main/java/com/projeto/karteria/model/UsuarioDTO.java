package com.projeto.karteria.model;

// Esta é uma classe simples, sem anotações de banco de dados ou segurança.
// Apenas com os campos que queremos expor na API.
public class UsuarioDTO {

    private Long id;
    private String nome;
    private String email;
    private String telefone;

    // Construtor
    public UsuarioDTO(Long id, String nome, String email, String telefone) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
}