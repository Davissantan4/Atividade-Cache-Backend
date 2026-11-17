package com.example.cacheconteudo.service;

import com.example.cacheconteudo.cache.ConteudoCache;
import com.example.cacheconteudo.cache.ConteudoCache.ResultadoCache;
import com.example.cacheconteudo.dto.CacheEstatisticasDTO;
import com.example.cacheconteudo.entity.Conteudo;
import com.example.cacheconteudo.repository.ConteudoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
public class ConteudoService {

    private final ConteudoRepository repository;
    private final ConteudoCache cache;

    public ConteudoService(ConteudoRepository repository, ConteudoCache cache) {
        this.repository = repository;
        this.cache = cache;
    }

    @Transactional(readOnly = true)
    public ResultadoCache buscarPorId(Long id) {
        return cache.buscar(id, () -> buscarNaFonte(id));
    }

    private Conteudo buscarNaFonte(Long id) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Busca interrompida.", e);
        }

        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Conteúdo não encontrado."));
    }

    @Transactional
    public Conteudo cadastrar(Conteudo conteudo) {
        conteudo.setAtualizadoEm(LocalDateTime.now());
        return repository.save(conteudo);
    }

    @Transactional
    public Conteudo atualizar(Long id, Conteudo dados) {
        Conteudo conteudo = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Conteúdo não encontrado."));

        conteudo.setTitulo(dados.getTitulo());
        conteudo.setCorpo(dados.getCorpo());
        conteudo.setAutor(dados.getAutor());
        conteudo.setAtualizadoEm(LocalDateTime.now());

        Conteudo atualizado = repository.save(conteudo);
        cache.invalidar(id);

        return atualizado;
    }

    @Transactional
    public void excluir(Long id) {
        Conteudo conteudo = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Conteúdo não encontrado."));

        repository.delete(conteudo);
        cache.invalidar(id);
    }

    public CacheEstatisticasDTO obterEstatisticas() {
        return cache.obterEstatisticas();
    }

    public void limparCache() {
        cache.limpar();
    }
}
