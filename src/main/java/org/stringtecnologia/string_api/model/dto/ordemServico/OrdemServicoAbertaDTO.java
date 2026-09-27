package org.stringtecnologia.string_api.model.dto.ordemServico;


import java.time.LocalDateTime;

public record OrdemServicoAbertaDTO(

        Long ordemServicoId,

        String numero,

        String statusCodigo,

        String statusDescricao,

        LocalDateTime dataAbertura,


        Long clienteId,

        String clienteNome,

        String clienteCpf,

        String clienteTelefone,


        Long aparelhoId,

        String marca,

        String modelo,

        String modeloComercial,

        String numeroSerie

) {
}
