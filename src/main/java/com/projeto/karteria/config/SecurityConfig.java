package com.projeto.karteria.config;

import com.projeto.karteria.service.ActiveProfileSecurityService;
import com.projeto.karteria.service.UsuarioService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity

// Habilita segurança baseada em métodos (anotações @PreAuthorize, etc.)
public class SecurityConfig {

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public SecurityFilterChain securityFilterChain(
      HttpSecurity http,
      UsuarioService usuarioService,
      ActiveProfileSecurityService activeProfileSecurityService)
      throws Exception {

    http.csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(
            authorize ->
                authorize
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
                    .access(
                        (authenticationSupplier, context) -> {
                          Authentication authentication = authenticationSupplier.get();
                          // Verifica se está autenticado (e não é anônimo)
                          boolean isAuthenticated =
                              authentication != null
                                  && authentication.isAuthenticated()
                                  && !(authentication instanceof AnonymousAuthenticationToken);
                          if (!isAuthenticated) {
                            return new AuthorizationDecision(false);
                          }
                          // Verifica Authority original
                          @SuppressWarnings("null")
                          boolean hasRequiredAuthority =
                              authentication.getAuthorities().stream()
                                  .anyMatch(
                                      grantedAuthority ->
                                          grantedAuthority.getAuthority().equals("EMPREGADOR"));
                          // Verifica Perfil Ativo na sessão usando o serviço injetado
                          boolean hasRequiredActiveRole =
                              activeProfileSecurityService.hasActiveRole("EMPREGADOR");

                          // Permite se tiver a Authority OU o Perfil Ativo correto
                          return new AuthorizationDecision(
                              hasRequiredAuthority || hasRequiredActiveRole);
                        })

                    // ** 3. REGRAS COLABORADOR
                    .requestMatchers("/candidatar/**")
                    .access(
                        (authenticationSupplier, context) -> { // Usa lambda
                          Authentication authentication = authenticationSupplier.get();
                          boolean isAuthenticated =
                              authentication != null
                                  && authentication.isAuthenticated()
                                  && !(authentication instanceof AnonymousAuthenticationToken);
                          if (!isAuthenticated) {
                            return new AuthorizationDecision(false);
                          }
                          @SuppressWarnings("null")
                          boolean hasRequiredAuthority =
                              authentication.getAuthorities().stream()
                                  .anyMatch(
                                      grantedAuthority ->
                                          grantedAuthority.getAuthority().equals("COLABORADOR"));
                          boolean hasRequiredActiveRole =
                              activeProfileSecurityService.hasActiveRole("COLABORADOR");

                          return new AuthorizationDecision(
                              hasRequiredAuthority || hasRequiredActiveRole);
                        })

                    // 4. QUALQUER OUTRA URL AUTENTICADA
                    .anyRequest()
                    .authenticated())
        // Configuração do login
        .formLogin(
            form ->
                form.loginPage("/login").defaultSuccessUrl("/escolher-perfil", true).permitAll())
        // Configuração do logout
        .logout(logout -> logout.logoutUrl("/logout").logoutSuccessUrl("/?logout").permitAll())
        .userDetailsService(usuarioService);

    // Construir a cadeia de filtros de segurança
    return http.build();
  }
}
