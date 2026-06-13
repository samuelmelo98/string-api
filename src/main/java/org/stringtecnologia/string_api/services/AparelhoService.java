package org.stringtecnologia.string_api.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.stringtecnologia.string_api.model.dto.aparelho.AparelhoRequestDTO;
import org.stringtecnologia.string_api.model.dto.aparelho.AparelhoResponseDTO;
import org.stringtecnologia.string_api.model.dto.aparelho.AparelhoStatusRequestDTO;
import org.stringtecnologia.string_api.model.entities.Aparelho;
import org.stringtecnologia.string_api.model.entities.DominioSistema;
import org.stringtecnologia.string_api.model.enums.Marca;
import org.stringtecnologia.string_api.repository.AparelhoRepository;
import org.stringtecnologia.string_api.util.DominioEnum;
import org.stringtecnologia.string_api.util.StatusAparelho;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AparelhoService implements AparelhoServiceI {
    private final AparelhoRepository aparelhoRepository;
    private final DominioSistemaService dominioSistemaService;

    @Override
    @Transactional
    public AparelhoResponseDTO criar(AparelhoRequestDTO request) {

        Aparelho aparelho = new Aparelho();

        aparelho.setMarca(Marca.SAMSUNG);
        aparelho.setModelo(request.modelo());
        aparelho.setModeloComercial(request.modeloComercial());
        aparelho.setNumeroSerie(request.numeroSerie());
        aparelho.setDescricao(request.descricao());
        aparelho.setTipo(request.tipo());
        aparelho.setDefeito(request.defeito());
        aparelho.setObservacao(request.observacao());
        aparelho.setFimGarantia(request.fimGarantia());
        aparelho.setStatusAparelho(
                dominioSistemaService.buscar(
                        StatusAparelho.PARA_ORCAMENTO
                )
        );

        aparelho = aparelhoRepository.save(aparelho);

        return this.toResponse(aparelho);
    }

    @Override
    public List<AparelhoResponseDTO> listar() {
        return List.of();
    }

    @Override
    public AparelhoResponseDTO buscarPorId(Long id) {
        return null;
    }

    @Override
    public AparelhoResponseDTO atualizar(Long id, AparelhoRequestDTO request) {
        return null;
    }

    @Override
    public AparelhoResponseDTO alterarStatus(Long id, AparelhoStatusRequestDTO request) {
        return null;
    }

    @Override
    public void excluir(Long id) {

    }

    public AparelhoResponseDTO toResponse(Aparelho aparelho) {

        return new AparelhoResponseDTO(
                aparelho.getAparelhoId(),
                aparelho.getMarca() != null
                        ? aparelho.getMarca().name()
                        : null,
                aparelho.getModelo(),
                aparelho.getModeloComercial(),
                aparelho.getNumeroSerie(),
                aparelho.getStatusAparelho() != null
                        ? aparelho.getStatusAparelho().getDominioSistemaId()
                        : null,
                aparelho.getStatusAparelho() != null
                        ? aparelho.getStatusAparelho().getDescricao()
                        : null,
                aparelho.getObservacao()
        );
    }
}
