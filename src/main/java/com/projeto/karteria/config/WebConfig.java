package com.projeto.karteria.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import java.time.Duration;
import java.util.Locale;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // 1. Define onde a escolha do idioma fica salva (no Cookie)
    @SuppressWarnings("deprecation")
    @Bean
    public LocaleResolver localeResolver() {
        CookieLocaleResolver cookieLocaleResolver = new CookieLocaleResolver("karteria_lang");
        cookieLocaleResolver.setDefaultLocale(new Locale("pt", "BR")); // Padrão: Português
        cookieLocaleResolver.setCookieMaxAge(Duration.ofDays(365)); // Lembrar por 1 ano
        return cookieLocaleResolver;
    }

    // 2. Define o interceptor que detecta a mudança na URL (?lang=xx)
    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
        lci.setParamName("lang"); // O nome do parâmetro na URL
        return lci;
    }

    // 3. Registra o interceptor
    @SuppressWarnings("null")
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
    }
}