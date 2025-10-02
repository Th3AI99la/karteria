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
            // A CORREÇÃO ESTÁ AQUI DENTRO
            .authorizeHttpRequests(authorize -> authorize
                // Regra 1: Permite acesso público a estas URLs
                .requestMatchers("/", "/login", "/register", "/css/**", "/js/**").permitAll()
                // Regra 2: Exige autenticação para URLs da API
                .requestMatchers("/api/**").authenticated()
                // Regra 3 (Padrão): Qualquer outra requisição também exige autenticação
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/home", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            )
            .userDetailsService(usuarioService);

        return http.build();
    }
}