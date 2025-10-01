package com.projeto.karteria.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String showIndexPage() {
        return "index"; // Página inicial pública
    }

    @GetMapping("/dashboard")
    public String showDashboard() {
        return "dashboard"; // Página principal após o login
    }
}