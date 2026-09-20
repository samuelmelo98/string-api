package org.stringtecnologia.string_api.services;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;


import org.stringtecnologia.string_api.model.dto.marca.MarcaResponse;
import org.stringtecnologia.string_api.model.entities.Marca;
import org.stringtecnologia.string_api.repository.MarcaRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MarcaService {

    private final MarcaRepository marcaRepository;

    public List<MarcaResponse> listarAtivas() {
        return marcaRepository.findByAtivoTrueOrderByNomeAsc()
                .stream()
                .map(marca -> new MarcaResponse(
                        marca.getMarcaId(),
                        marca.getNome()
                ))
                .toList();
    }

    public Marca buscarAtiva(Long marcaId) {
        if (marcaId == null || marcaId <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Informe uma marca válida."
            );
        }

        Marca marca = marcaRepository.findById(marcaId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Marca não encontrada."
                ));

        if (!marca.isAtivo()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A marca selecionada está inativa."
            );
        }

        return marca;
    }
}
