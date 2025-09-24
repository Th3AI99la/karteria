package com.projeto.karteria.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller 
public class HomeController {

    @GetMapping("/") // Mapeia a rota raiz (http://localhost:8080/)
    public String index() {
        return "index";
    }
}