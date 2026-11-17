package com.example.cacheconteudo.dto;

import jakarta.validation.constraints.NotBlank;

public record ConteudoRequestDTO(
        @NotBlank String titulo,
        @NotBlank String corpo,
        @NotBlank String autor
) {
}
