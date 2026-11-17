package com.example.cacheconteudo.controller;

import com.example.cacheconteudo.cache.ConteudoCache.ResultadoCache;
import com.example.cacheconteudo.dto.ConteudoRequestDTO;
import com.example.cacheconteudo.dto.ConteudoResponseDTO;
import com.example.cacheconteudo.entity.Conteudo;
import com.example.cacheconteudo.mapper.ConteudoMapper;
import com.example.cacheconteudo.service.ConteudoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/conteudos")
public class ConteudoController {

    private final ConteudoService service;
    private final ConteudoMapper mapper;

    public ConteudoController(ConteudoService service, ConteudoMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConteudoResponseDTO> buscar(@PathVariable Long id) {
        ResultadoCache resultado = service.buscarPorId(id);
        String statusCache = resultado.hit() ? "HIT" : "MISS";

        return ResponseEntity.ok()
                .header("X-Cache", statusCache)
                .body(mapper.toResponseDTO(resultado.conteudo()));
    }

    @PostMapping
    public ResponseEntity<ConteudoResponseDTO> cadastrar(
            @Valid @RequestBody ConteudoRequestDTO dto) {

        Conteudo conteudo = mapper.toEntity(dto);
        Conteudo salvo = service.cadastrar(conteudo);

        return ResponseEntity.status(201)
                .body(mapper.toResponseDTO(salvo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ConteudoResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody ConteudoRequestDTO dto) {

        Conteudo dados = mapper.toEntity(dto);
        Conteudo atualizado = service.atualizar(id, dados);

        return ResponseEntity.ok(mapper.toResponseDTO(atualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
