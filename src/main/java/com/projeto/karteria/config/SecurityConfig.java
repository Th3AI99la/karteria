package com.projeto.karteria.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.projeto.karteria.service.ActiveProfileSecurityService;
import com.projeto.karteria.service.UsuarioService;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    // ======================================================================
    // 1. API (JWT)
    // ======================================================================
    @Bean
    @Order(1)
    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/api/**")
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // ======================================================================
    // 2. WEB (SESSION)
    // ======================================================================
    @Bean
    @Order(2)
    public SecurityFilterChain webFilterChain(
            HttpSecurity http,
            UsuarioService usuarioService,
            ActiveProfileSecurityService activeProfileSecurityService)
            throws Exception {

        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authorize -> authorize

                        // =========================
                        // 1. PÚBLICO
                        // =========================
                        .requestMatchers(
                                "/",
                                "/login",
                                "/register",
                                "/esqueci-senha",
                                "/resetar-senha",
                                "/completar-cadastro",
                                "/css/**",
                                "/js/**",
                                "/images/**")
                        .permitAll()

                        // =========================
                        // 2. EMPREGADOR (restrito)
                        // =========================
                        .requestMatchers(
                                "/anuncios/novo",
                                "/anuncios/salvar",
                                "/anuncios/editar/**",
                                "/anuncios/apagar/**",
                                "/anuncios/status/**",
                                "/anuncios/arquivar/**",
                                "/anuncios/desarquivar/**")
                        .access((authenticationSupplier, context) -> {
                            Authentication authentication = authenticationSupplier.get();

                            boolean isAuthenticated = authentication != null
                                    && authentication.isAuthenticated()
                                    && !(authentication instanceof AnonymousAuthenticationToken);

                            if (!isAuthenticated)
                                return new AuthorizationDecision(false);

                            boolean hasAuthority = authentication.getAuthorities().stream()
                                    .anyMatch(a -> a.getAuthority().equals("EMPREGADOR"));

                            boolean hasActiveRole =
                                    activeProfileSecurityService.hasActiveRole("EMPREGADOR");

                            return new AuthorizationDecision(hasAuthority || hasActiveRole);
                        })

                        // =========================
                        // 3. GERENCIAR (AMBOS)
                        // =========================
                        .requestMatchers("/anuncios/gerenciar/**")
                        .access((authenticationSupplier, context) -> {
                            Authentication authentication = authenticationSupplier.get();

                            boolean isAuthenticated = authentication != null
                                    && authentication.isAuthenticated()
                                    && !(authentication instanceof AnonymousAuthenticationToken);

                            if (!isAuthenticated)
                                return new AuthorizationDecision(false);

                            boolean hasAuthority = authentication.getAuthorities().stream()
                                    .anyMatch(a ->
                                            a.getAuthority().equals("EMPREGADOR") ||
                                            a.getAuthority().equals("COLABORADOR"));

                            boolean hasActiveRole =
                                    activeProfileSecurityService.hasActiveRole("EMPREGADOR") ||
                                    activeProfileSecurityService.hasActiveRole("COLABORADOR");

                            return new AuthorizationDecision(hasAuthority || hasActiveRole);
                        })

                        // =========================
                        // 4. COLABORADOR
                        // =========================
                        .requestMatchers("/candidatar/**")
                        .access((authenticationSupplier, context) -> {
                            Authentication authentication = authenticationSupplier.get();

                            boolean isAuthenticated = authentication != null
                                    && authentication.isAuthenticated()
                                    && !(authentication instanceof AnonymousAuthenticationToken);

                            if (!isAuthenticated)
                                return new AuthorizationDecision(false);

                            boolean hasAuthority = authentication.getAuthorities().stream()
                                    .anyMatch(a -> a.getAuthority().equals("COLABORADOR"));

                            boolean hasActiveRole =
                                    activeProfileSecurityService.hasActiveRole("COLABORADOR");

                            return new AuthorizationDecision(hasAuthority || hasActiveRole);
                        })

                        // =========================
                        // 5. COMUM AUTENTICADO
                        // =========================
                        .requestMatchers("/notificacoes/**", "/ws/**")
                        .authenticated()

                        .anyRequest().authenticated()
                )

                // LOGIN
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/escolher-perfil", true)
                        .permitAll()
                )

                // LOGOUT
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/?logout")
                        .permitAll()
                )

                .userDetailsService(usuarioService);

        return http.build();
    }
}