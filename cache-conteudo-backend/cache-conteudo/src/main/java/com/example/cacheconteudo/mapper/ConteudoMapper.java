package com.example.cacheconteudo.mapper;

import com.example.cacheconteudo.dto.ConteudoRequestDTO;
import com.example.cacheconteudo.dto.ConteudoResponseDTO;
import com.example.cacheconteudo.entity.Conteudo;
import org.springframework.stereotype.Component;

@Component
public class ConteudoMapper {

    public Conteudo toEntity(ConteudoRequestDTO dto) {
        Conteudo conteudo = new Conteudo();
        conteudo.setTitulo(dto.titulo());
        conteudo.setCorpo(dto.corpo());
        conteudo.setAutor(dto.autor());
        return conteudo;
    }

    public ConteudoResponseDTO toResponseDTO(Conteudo conteudo) {
        return new ConteudoResponseDTO(
                conteudo.getId(),
                conteudo.getTitulo(),
                conteudo.getCorpo(),
                conteudo.getAutor(),
                conteudo.getAtualizadoEm()
        );
    }
}
