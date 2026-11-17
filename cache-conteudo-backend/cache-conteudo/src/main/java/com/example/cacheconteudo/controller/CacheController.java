package com.example.cacheconteudo.controller;

import com.example.cacheconteudo.dto.CacheEstatisticasDTO;
import com.example.cacheconteudo.service.ConteudoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cache")
public class CacheController {

    private final ConteudoService service;

    public CacheController(ConteudoService service) {
        this.service = service;
    }

    @GetMapping("/estatisticas")
    public ResponseEntity<CacheEstatisticasDTO> estatisticas() {
        return ResponseEntity.ok(service.obterEstatisticas());
    }

    @DeleteMapping
    public ResponseEntity<Void> limpar() {
        service.limparCache();
        return ResponseEntity.noContent().build();
    }
}
