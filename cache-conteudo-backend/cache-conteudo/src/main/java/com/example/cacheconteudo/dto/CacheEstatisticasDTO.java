package com.example.cacheconteudo.dto;

public record CacheEstatisticasDTO(
        int tamanhoAtual,
        int limite,
        long hits,
        long misses,
        long evictions,
        double hitRatio
) {
}
