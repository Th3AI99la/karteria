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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.projeto.karteria.service.ActiveProfileSecurityService;
import com.projeto.karteria.service.UsuarioService;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;

    // Injeta o nosso novo porteiro
    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }
    // ======================================================================
    // 1. CONFIGURAÇÃO DA API MOBILE (REST + JWT)
    // ======================================================================
    @Bean
    @Order(1) // O Spring vai ler esta regra PRIMEIRO
    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/api/**") // Aplica estas regras APENAS a rotas que comecem por /api/
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Desliga
                                                                                                              // as
                                                                                                              // sessões
                                                                                                              // para a
                                                                                                              // app
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll() // Rota de login da app é pública
                        .anyRequest().authenticated() // Qualquer outra rota da app requer token JWT válido
                )
                // Coloca o nosso filtro JWT antes do filtro padrão do Spring
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // ======================================================================
    // 2. CONFIGURAÇÃO WEB (THYMELEAF + SESSÃO DE NAVEGADOR)
    // ======================================================================
    @Bean
    @Order(2) // O Spring vai ler esta regra em SEGUNDO LUGAR (se a rota não começar por
              // /api/)
    public SecurityFilterChain webFilterChain(
            HttpSecurity http,
            UsuarioService usuarioService,
            ActiveProfileSecurityService activeProfileSecurityService)
            throws Exception {

        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authorize -> authorize
                        // 1. REGRAS PÚBLICAS
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

                        // 2. REGRAS EMPREGADOR
                        .requestMatchers(
                                "/anuncios/novo",
                                "/anuncios/salvar",
                                "/anuncios/editar/**",
                                "/anuncios/apagar/**",
                                "/anuncios/status/**",
                                "/anuncios/arquivar/**",
                                "/anuncios/desarquivar/**",
                                "/anuncios/gerenciar/**")
                        .access((authenticationSupplier, context) -> {
                            Authentication authentication = authenticationSupplier.get();
                            boolean isAuthenticated = authentication != null
                                    && authentication.isAuthenticated()
                                    && !(authentication instanceof AnonymousAuthenticationToken);
                            if (!isAuthenticated)
                                return new AuthorizationDecision(false);

                            @SuppressWarnings("null")
                            boolean hasRequiredAuthority = authentication.getAuthorities().stream()
                                    .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("EMPREGADOR"));
                            boolean hasRequiredActiveRole = activeProfileSecurityService.hasActiveRole("EMPREGADOR");

                            return new AuthorizationDecision(hasRequiredAuthority || hasRequiredActiveRole);
                        })

                        // 3. REGRAS COLABORADOR
                        .requestMatchers("/candidatar/**")
                        .access((authenticationSupplier, context) -> {
                            Authentication authentication = authenticationSupplier.get();
                            boolean isAuthenticated = authentication != null
                                    && authentication.isAuthenticated()
                                    && !(authentication instanceof AnonymousAuthenticationToken);
                            if (!isAuthenticated)
                                return new AuthorizationDecision(false);

                            @SuppressWarnings("null")
                            boolean hasRequiredAuthority = authentication.getAuthorities().stream()
                                    .anyMatch(
                                            grantedAuthority -> grantedAuthority.getAuthority().equals("COLABORADOR"));
                            boolean hasRequiredActiveRole = activeProfileSecurityService.hasActiveRole("COLABORADOR");

                            return new AuthorizationDecision(hasRequiredAuthority || hasRequiredActiveRole);
                        })

                        // 4. QUALQUER OUTRA URL AUTENTICADA (Site)
                        .anyRequest().authenticated())

                // Configuração do login web normal
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/escolher-perfil", true)
                        .permitAll())

                // Configuração do logout web normal
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/?logout")
                        .permitAll())
                .userDetailsService(usuarioService);

        return http.build();
    }
}