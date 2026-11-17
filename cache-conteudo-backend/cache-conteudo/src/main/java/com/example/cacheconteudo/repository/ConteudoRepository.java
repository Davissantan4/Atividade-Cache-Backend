package com.example.cacheconteudo.repository;

import com.example.cacheconteudo.entity.Conteudo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConteudoRepository extends JpaRepository<Conteudo, Long> {
}
