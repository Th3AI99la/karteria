package com.projeto.karteria.controller.api;

import com.projeto.karteria.dto.AnuncioResponseDTO;
import com.projeto.karteria.model.Anuncio;
import com.projeto.karteria.model.StatusAnuncio;
import com.projeto.karteria.repository.AnuncioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/anuncios")
public class AnuncioRestController {

    @Autowired
    private AnuncioRepository anuncioRepository;

    @GetMapping
    public ResponseEntity<List<AnuncioResponseDTO>> listarAnunciosAtivos() {
        List<Anuncio> todosAnuncios = anuncioRepository.findAll();

        List<AnuncioResponseDTO> response = todosAnuncios.stream()
                .filter(anuncio -> anuncio.getStatus() == StatusAnuncio.ATIVO)
                .map(anuncio -> new AnuncioResponseDTO(
                        anuncio.getId(),
                        anuncio.getTitulo(),
                        anuncio.getDescricao(),
                        anuncio.getLocalizacao(),
                        anuncio.getValorMin(),
                        anuncio.getValorMax(),
                        anuncio.getTipoPagamento(),
                        anuncio.getAnunciante() != null ? anuncio.getAnunciante().getNome() : "Desconhecido",
                        anuncio.getDataPostagem()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }
}