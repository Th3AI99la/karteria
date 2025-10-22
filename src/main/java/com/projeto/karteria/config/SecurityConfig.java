package com.projeto.karteria.config;

import com.projeto.karteria.service.UsuarioService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http, UsuarioService usuarioService)
      throws Exception {
    http.csrf(csrf -> csrf.disable()) // Desativa CSRF para testes e APIs
        .authorizeHttpRequests(
            authorize ->
                authorize

                    // ================== PONTO CRÍTICO DA CORREÇÃO ==================
                    // 1. URLs PÚBLICAS: Todas as URLs listadas aqui são 100% livres.
                    .requestMatchers(
                        "/", // Página inicial
                        "/login", // Página de login
                        "/register", // Página de registro
                        "/esqueci-senha", // Página para solicitar reset
                        "/resetar-senha", // Página para definir nova senha
                        "/css/**", // Arquivos CSS
                        "/js/**", // Arquivos JS
                        "/images/**" // Pasta para imagens, se houver
                        )
                    .permitAll()

                    // 2. QUALQUER OUTRA URL: Todas as outras URLs não listadas acima exigem
                    // autenticação.
                    .anyRequest()
                    .authenticated()
            // =============================================================
            )
        .formLogin(
            form ->
                form.loginPage("/login") // Define a página de login customizada
                    .defaultSuccessUrl("/escolher-perfil", true) // Para onde ir após o login
                    .permitAll())
        .logout(
            logout ->
                logout
                    .logoutUrl("/logout")
                    .logoutSuccessUrl("/?logout") // Para onde ir após o logout
                    .permitAll())
        .userDetailsService(usuarioService);

    return http.build();
  }
}
