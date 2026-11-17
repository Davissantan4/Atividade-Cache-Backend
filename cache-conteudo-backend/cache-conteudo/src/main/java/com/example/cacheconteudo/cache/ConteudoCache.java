package com.example.cacheconteudo.cache;

import com.example.cacheconteudo.dto.CacheEstatisticasDTO;
import com.example.cacheconteudo.entity.Conteudo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

@Component
public class ConteudoCache {

    private static final Logger logger =
            LoggerFactory.getLogger(ConteudoCache.class);

    private final ConcurrentHashMap<Long, EntradaCache> cache =
            new ConcurrentHashMap<>();

    private final int maxItens;
    private final long ttlMilissegundos;

    private long hits = 0;
    private long misses = 0;
    private long evictions = 0;

    public ConteudoCache(
            @Value("${app.cache.max-itens}") int maxItens,
            @Value("${app.cache.ttl-segundos}") long ttlSegundos) {

        if (maxItens < 1 || ttlSegundos < 1) {
            throw new IllegalArgumentException(
                    "O limite e o TTL do cache devem ser maiores que zero.");
        }

        this.maxItens = maxItens;
        this.ttlMilissegundos = ttlSegundos * 1000;
    }

    public synchronized ResultadoCache buscar(
            Long id, Supplier<Conteudo> fonte) {

        long inicio = System.currentTimeMillis();
        EntradaCache entrada = cache.get(id);

        if (entrada != null) {
            long agora = System.currentTimeMillis();

            if (agora - entrada.getArmazenadoEm() < ttlMilissegundos) {
                entrada.setUltimoAcesso(agora);
                hits++;

                long tempo = System.currentTimeMillis() - inicio;
                logger.info("Cache HIT - id={} - {} ms", id, tempo);

                return new ResultadoCache(entrada.getConteudo(), true);
            }

            cache.remove(id);
        }

        misses++;

        Conteudo conteudo = fonte.get();

        if (cache.size() >= maxItens) {
            Long idMenosUsado = cache.entrySet()
                    .stream()
                    .min(Comparator.comparingLong(
                            item -> item.getValue().getUltimoAcesso()))
                    .map(item -> item.getKey())
                    .orElse(null);

            if (idMenosUsado != null) {
                cache.remove(idMenosUsado);
                evictions++;
            }
        }

        long agora = System.currentTimeMillis();
        cache.put(id, new EntradaCache(conteudo, agora));

        long tempo = System.currentTimeMillis() - inicio;
        logger.info("Cache MISS - id={} - {} ms", id, tempo);

        return new ResultadoCache(conteudo, false);
    }

    public synchronized void invalidar(Long id) {
        cache.remove(id);
    }

    public synchronized void limpar() {
        cache.clear();
    }

    public synchronized CacheEstatisticasDTO obterEstatisticas() {
        long total = hits + misses;

        double hitRatio = total == 0
                ? 0.0
                : (double) hits / total;

        return new CacheEstatisticasDTO(
                cache.size(),
                maxItens,
                hits,
                misses,
                evictions,
                hitRatio
        );
    }

    public record ResultadoCache(Conteudo conteudo, boolean hit) {
    }

    private static class EntradaCache {

        private final Conteudo conteudo;
        private final long armazenadoEm;
        private long ultimoAcesso;

        public EntradaCache(Conteudo conteudo, long agora) {
            this.conteudo = conteudo;
            this.armazenadoEm = agora;
            this.ultimoAcesso = agora;
        }

        public Conteudo getConteudo() {
            return conteudo;
        }

        public long getArmazenadoEm() {
            return armazenadoEm;
        }

        public long getUltimoAcesso() {
            return ultimoAcesso;
        }

        public void setUltimoAcesso(long ultimoAcesso) {
            this.ultimoAcesso = ultimoAcesso;
        }
    }
}
