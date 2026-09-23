package org.stringtecnologia.string_api.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.stringtecnologia.string_api.model.dto.aparelho.AparelhoRequestDTO;
import org.stringtecnologia.string_api.model.dto.aparelho.AparelhoResponseDTO;
import org.stringtecnologia.string_api.model.dto.aparelho.AparelhoStatusRequestDTO;
import org.stringtecnologia.string_api.model.dto.aparelho.AparelhoUpdateDTO;

import java.util.List;

public interface AparelhoServiceI {

    AparelhoResponseDTO criar(
            AparelhoRequestDTO request
    );

    List<AparelhoResponseDTO> listar();

    AparelhoResponseDTO buscarPorId(
            Long id
    );

    AparelhoResponseDTO atualizar(
            Long id,
            AparelhoUpdateDTO request
    );

    AparelhoResponseDTO alterarStatus(
            Long id,
            AparelhoStatusRequestDTO request
    );

    void excluir(
            Long id
    );

    Page<AparelhoResponseDTO> listarPorCliente(
            Long clienteId,
            Pageable pageable
    );
}