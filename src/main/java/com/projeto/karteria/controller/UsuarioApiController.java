package com.projeto.karteria.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projeto.karteria.model.UsuarioDTO;
import com.projeto.karteria.service.UsuarioService;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioApiController {

    @Autowired
    private UsuarioService usuarioService;

      @GetMapping
    // O retorno agora é uma lista de UsuarioDTO
    public List<UsuarioDTO> getAllUsers() {
        // Chamamos o novo método que retorna DTOs
        return usuarioService.listarTodosDTO();
    }
}