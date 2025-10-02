package com.projeto.karteria.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String showIndexPage() {
        return "index"; // Mostra a página pública inicial
    }

    @GetMapping("/home")
    public String showHomePage() {
        return "home"; // Mostra a página principal após o login
    }
}