package com.projeto.karteria.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.projeto.karteria.service.UsuarioService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, UsuarioService usuarioService) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                // Libera o acesso à página pública, login, registro e arquivos estáticos
                .requestMatchers("/", "/login", "/register", "/css/**", "/js/**").permitAll()
                // Qualquer outra requisição (como /home) precisa de autenticação
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login") // Nossa página de login customizada
                .defaultSuccessUrl("/home", true) // Redireciona para /home após sucesso
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout") // Redireciona para login com msg de logout
                .permitAll()
            )
            .userDetailsService(usuarioService);

        return http.build();
    }
}