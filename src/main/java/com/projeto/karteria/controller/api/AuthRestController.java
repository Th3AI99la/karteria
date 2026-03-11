package com.projeto.karteria.controller.api;

import com.projeto.karteria.dto.AuthRequestDTO;
import com.projeto.karteria.dto.AuthResponseDTO;
import com.projeto.karteria.model.Usuario;
import com.projeto.karteria.repository.UsuarioRepository;
import com.projeto.karteria.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthRestController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequestDTO request) {
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail()).orElse(null);

        if (usuario == null || !passwordEncoder.matches(request.getSenha(), usuario.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("E-mail ou senha inválidos.");
        }

        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("id", usuario.getId());
        extraClaims.put("tipo", usuario.getTipo() != null ? usuario.getTipo().name() : "NENHUM");

        String token = jwtService.generateToken(extraClaims, usuario);

        AuthResponseDTO response = new AuthResponseDTO(
                token,
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getTipo() != null ? usuario.getTipo().name() : null);

        return ResponseEntity.ok(response);
    }
}