package com.projeto.karteria.dto;

public class AuthResponseDTO {
    private String token;
    private Long id;
    private String nome;
    private String email;
    private String tipoUsuario;

    public AuthResponseDTO(String token, Long id, String nome, String email, String tipoUsuario) {
        this.token = token;
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.tipoUsuario = tipoUsuario;
    }

    // Getters
    public String getToken() { return token; }
    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public String getTipoUsuario() { return tipoUsuario; }
}