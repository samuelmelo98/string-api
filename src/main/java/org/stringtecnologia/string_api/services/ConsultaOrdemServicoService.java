package org.stringtecnologia.string_api.services;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.stringtecnologia.string_api.model.dto.ordemServico.ConsultaOrdemServicoRequestDTO;
import org.stringtecnologia.string_api.model.dto.ordemServico.ConsultaOrdemServicoResponseDTO;
import org.stringtecnologia.string_api.model.entities.Aparelho;
import org.stringtecnologia.string_api.model.entities.OrdemServico;
import org.stringtecnologia.string_api.repository.OrdemServicoRepository;

@Service
@RequiredArgsConstructor
public class ConsultaOrdemServicoService {

    private final OrdemServicoRepository ordemServicoRepository;

    @Transactional(readOnly = true)
    public ConsultaOrdemServicoResponseDTO consultar(
            String numero,
            ConsultaOrdemServicoRequestDTO request
    ) {

        validarEntrada(
                numero,
                request
        );

        OrdemServico ordem =
                ordemServicoRepository
                        .findByNumeroAndConsultaToken(
                                numero.trim(),
                                request.token().trim()
                        )
                        .orElseThrow(
                                this::acessoNegado
                        );

        validarCpf(
                ordem,
                request.ultimosDigitosCpf()
        );

        return toResponse(
                ordem
        );
    }

    private void validarEntrada(
            String numero,
            ConsultaOrdemServicoRequestDTO request
    ) {

        if (numero == null
                || numero.isBlank()
                || request == null) {

            throw acessoNegado();
        }
    }

    private void validarCpf(
            OrdemServico ordem,
            String ultimosDigitosCpf
    ) {

        if (ordem.getCliente() == null
                || ordem.getCliente().getCpf() == null
                || ordem.getCliente().getCpf().isBlank()) {

            throw acessoNegado();
        }

        String cpf =
                ordem.getCliente()
                        .getCpf()
                        .replaceAll(
                                "\\D",
                                ""
                        );

        if (cpf.length() != 11) {
            throw acessoNegado();
        }

        if (!cpf.endsWith(
                ultimosDigitosCpf
        )) {

            throw acessoNegado();
        }
    }

    private ConsultaOrdemServicoResponseDTO toResponse(
            OrdemServico ordem
    ) {

        Aparelho aparelho =
                ordem.getAparelho();

        return new ConsultaOrdemServicoResponseDTO(

                ordem.getNumero(),

                ordem.getCliente()
                        .getNome(),

                aparelho != null
                        && aparelho.getMarca() != null
                        ? aparelho.getMarca().getNome()
                        : null,

                aparelho != null
                        ? aparelho.getModelo()
                        : null,

                aparelho != null
                        ? aparelho.getModeloComercial()
                        : null,

                aparelho != null
                        ? aparelho.getNumeroSerie()
                        : null,

                ordem.getStatus() != null
                        ? ordem.getStatus().getCodigo()
                        : null,

                ordem.getStatus() != null
                        ? ordem.getStatus().getDescricao()
                        : null,

                ordem.getDefeitoRelatado(),

                ordem.getDiagnostico(),

                ordem.getSolucao(),

                ordem.getObservacao(),

                ordem.getValorOrcamento(),

                ordem.getValorFinal(),

                ordem.getDataAbertura(),

                ordem.getDataAprovacao(),

                ordem.getDataInicioServico(),

                ordem.getDataConclusao(),

                ordem.getDataEntrega()
        );
    }

    private ResponseStatusException acessoNegado() {

        return new ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "Dados de consulta inválidos."
        );
    }
}