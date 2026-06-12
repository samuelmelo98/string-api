package org.stringtecnologia.string_api.services;

import org.stringtecnologia.string_api.model.dto.aparelho.AparelhoRequestDTO;
import org.stringtecnologia.string_api.model.dto.aparelho.AparelhoResponseDTO;
import org.stringtecnologia.string_api.model.dto.aparelho.AparelhoStatusRequestDTO;

import java.util.List;

public interface AparelhoServiceI {

    AparelhoResponseDTO criar(AparelhoRequestDTO request);

    List<AparelhoResponseDTO> listar();

    AparelhoResponseDTO buscarPorId(Long id);

    AparelhoResponseDTO atualizar(Long id, AparelhoRequestDTO request);

    AparelhoResponseDTO alterarStatus(Long id,
                                      AparelhoStatusRequestDTO request);

    void excluir(Long id);
}
