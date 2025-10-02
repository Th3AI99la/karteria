package com.projeto.karteria.service;

import java.util.List; // <-- Adicionar este import
import java.util.stream.Collectors; // <-- Importar DTO

import org.springframework.beans.factory.annotation.Autowired; // <-- Importar Collectors
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.projeto.karteria.model.Usuario;
import com.projeto.karteria.model.UsuarioDTO;
import com.projeto.karteria.repository.UsuarioRepository;

@Service
public class UsuarioService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com o e-mail: " + email));
    }

    public void registerUser(Usuario usuario) {
        if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            throw new IllegalStateException("E-mail já cadastrado.");
        }
        usuario.setSenha(passwordEncoder.encode(usuario.getPassword()));
        usuarioRepository.save(usuario);
    }
    
    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }


      // NOVO MÉTODO: Retorna a lista convertida para DTOs
    public List<UsuarioDTO> listarTodosDTO() {
        return usuarioRepository.findAll() // 1. Busca todos os usuários
                .stream()                  // 2. Transforma em um fluxo de dados
                .map(this::convertToDto)   // 3. Converte cada usuário para DTO
                .collect(Collectors.toList()); // 4. Coleta em uma nova lista
    }

    // Método auxiliar para fazer a conversão
    private UsuarioDTO convertToDto(Usuario usuario) {
        return new UsuarioDTO(
            usuario.getId(),
            usuario.getNome(),
            usuario.getEmail(),
            usuario.getTelefone()
        );
    }
}