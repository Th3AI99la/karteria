package com.projeto.karteria.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.projeto.karteria.service.JwtService;
import com.projeto.karteria.service.UsuarioService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UsuarioService usuarioService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // 1. Procura o cabeçalho "Authorization" na requisição que chegou
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        // 2. Se não tiver cabeçalho ou não começar com "Bearer " (padrão de tokens), passa direto
        // Isso permite que o fluxo Web normal (Thymeleaf) continue a funcionar!
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Extrai o token (ignora os 7 primeiros caracteres: "Bearer ")
        jwt = authHeader.substring(7);
        userEmail = jwtService.extractUsername(jwt);

        // 4. Se encontrou um email no token e o utilizador ainda não está autenticado nesta requisição
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Busca os dados do utilizador na base de dados
            UserDetails userDetails = this.usuarioService.loadUserByUsername(userEmail);

            // 5. Valida se o token não está expirado e pertence realmente a este utilizador
            if (jwtService.isTokenValid(jwt, userDetails)) {
                
                // Cria o objeto de utilizador logado para o Spring Security entender
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                
                // Grava o utilizador logado no contexto de segurança daquela exata requisição (Stateless)
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        
        // 6. Continua a requisição para chegar ao Controller correto
        filterChain.doFilter(request, response);
    }
}