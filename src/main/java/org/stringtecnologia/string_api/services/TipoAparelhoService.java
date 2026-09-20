package org.stringtecnologia.string_api.services;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import org.stringtecnologia.string_api.model.dto.aparelho.TipoAparelhoResponseDTO;
import org.stringtecnologia.string_api.model.entities.TipoAparelho;
import org.stringtecnologia.string_api.repository.TipoAparelhoRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TipoAparelhoService {

    private final TipoAparelhoRepository tipoAparelhoRepository;

    public List<TipoAparelhoResponseDTO> listarAtivos() {
        return tipoAparelhoRepository
                .findByAtivoTrueOrderByNomeAsc()
                .stream()
                .map(tipo -> new TipoAparelhoResponseDTO(
                        tipo.getTipoAparelhoId(),
                        tipo.getNome()
                ))
                .toList();
    }

    public TipoAparelho buscarAtivo(Long tipoAparelhoId) {
        if (tipoAparelhoId == null || tipoAparelhoId <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Informe um tipo de aparelho válido."
            );
        }

        TipoAparelho tipo = tipoAparelhoRepository
                .findById(tipoAparelhoId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Tipo de aparelho não encontrado."
                ));

        if (!tipo.isAtivo()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O tipo de aparelho está inativo."
            );
        }

        return tipo;
    }
}