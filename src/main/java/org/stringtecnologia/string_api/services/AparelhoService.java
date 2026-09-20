package org.stringtecnologia.string_api.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.stringtecnologia.string_api.model.dto.aparelho.AparelhoRequestDTO;
import org.stringtecnologia.string_api.model.dto.aparelho.AparelhoResponseDTO;
import org.stringtecnologia.string_api.model.dto.aparelho.AparelhoStatusRequestDTO;
import org.stringtecnologia.string_api.model.entities.*;
import org.stringtecnologia.string_api.model.factory.aparelho.AparelhoFactory;
import org.stringtecnologia.string_api.repository.AparelhoRepository;
import org.stringtecnologia.string_api.repository.ClienteRepository;
import org.stringtecnologia.string_api.util.StatusAparelho;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AparelhoService implements AparelhoServiceI {
    private final AparelhoRepository aparelhoRepository;
    private final DominioSistemaService dominioSistemaService;
    private final ClienteRepository clienteRepository;
    private final MarcaService marcaService;
    private final AparelhoFactory  aparelhoFactory;
    private final TipoAparelhoService tipoAparelhoService;

    @Override
    @Transactional
    public AparelhoResponseDTO criar(AparelhoRequestDTO request) {
        Long clienteId = request.clienteId();

        if (clienteId == null || clienteId <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Informe um cliente válido."
            );
        }

        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Cliente não encontrado."
                ));

        Marca marca = marcaService.buscarAtiva(
                request.marcaId()
        );

        TipoAparelho tipoAparelho = tipoAparelhoService.buscarAtivo(
                request.tipoAparelhoId()
        );

        DominioSistema statusInicial = dominioSistemaService.buscar(
                StatusAparelho.PARA_ORCAMENTO
        );

        Aparelho aparelho = aparelhoFactory.criar(
                request,
                marca,
                tipoAparelho,
                statusInicial
        );

        aparelho.setCliente(cliente);

        Aparelho salvo = aparelhoRepository.save(aparelho);

        return toResponse(salvo);
    }

    @Override
    public List<AparelhoResponseDTO> listar() {

        return aparelhoRepository
                .findAll()
                .stream()
                .map(this::toResponse)
                .toList();
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
                        ? aparelho.getMarca().getNome()
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
                aparelho.getDataCadastro(),
                aparelho.getObservacao()
        );
    }

    @Override
    public Page<AparelhoResponseDTO> listarPorCliente(
            Long clienteId,
            Pageable pageable
    ) {

        return aparelhoRepository
                .findByClienteClienteId(
                        clienteId,
                        pageable
                )
                .map(this::toResponse);
    }


}
