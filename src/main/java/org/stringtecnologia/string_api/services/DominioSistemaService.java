package org.stringtecnologia.string_api.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.stringtecnologia.string_api.model.dto.dominio.sistema.DominioSistemaResponseDTO;
import org.stringtecnologia.string_api.model.entities.DominioSistema;
import org.stringtecnologia.string_api.repository.SistemaDominioRepository;
import org.stringtecnologia.string_api.util.CategoriaDominio;
import org.stringtecnologia.string_api.util.DominioEnum;
import org.stringtecnologia.string_api.util.exceptions.DominioNaoEncontradoException;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class DominioSistemaService {

    private final SistemaDominioRepository sistemaDominioRepository;
    private final Map<DominioEnum, DominioSistema> cache = new ConcurrentHashMap<>();

    public DominioSistemaService(SistemaDominioRepository sistemaDominioRepository) {
        this.sistemaDominioRepository = sistemaDominioRepository;
    }

    public DominioSistema buscarDominio(
            String categoria,
            String codigo) {

        return sistemaDominioRepository
                .findByCategoriaAndCodigoAndAtivoTrue(categoria, codigo)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Domínio não encontrado: "
                                        + categoria + " - " + codigo));
    }

    public DominioSistema buscar(DominioEnum dominioEnum) {
        return sistemaDominioRepository
                .findByCategoriaAndCodigoAndAtivoTrue(
                        dominioEnum.getCategoria().name(),
                        dominioEnum.getCodigo()
                )
                .orElseThrow(() ->
                        new DominioNaoEncontradoException(
                                dominioEnum.getCategoria(),
                                dominioEnum.getCodigo()
                        )
                );
    }


    public DominioSistema buscarDominioCache(DominioEnum dominioEnum) {
        return cache.computeIfAbsent(dominioEnum, this::buscarNoBanco);
    }

    private String gerarChave(DominioEnum dominioEnum) {
        return dominioEnum.getCategoria().name() + "_" + dominioEnum.getCodigo();
    }

    private DominioSistema buscarNoBanco(DominioEnum dominioEnum) {

        return sistemaDominioRepository
                .findByCategoriaAndCodigoAndAtivoTrue(
                        dominioEnum.getCategoria().name(),
                        dominioEnum.getCodigo()
                )
                .orElseThrow(() ->
                        new DominioNaoEncontradoException(
                                dominioEnum.getCategoria(),
                                dominioEnum.getCodigo()
                        )
                );
    }

    @Transactional(readOnly = true)
    public List<DominioSistemaResponseDTO> listarTodosPorCategoria(CategoriaDominio categoria) {
        return sistemaDominioRepository
                .findByCategoriaAndAtivoTrueOrderByDescricaoAsc(categoria.name())
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    private DominioSistemaResponseDTO toResponseDTO(DominioSistema e) {
        return new DominioSistemaResponseDTO(
                e.getDominioSistemaId(),
                e.getCategoria(),
                e.getCodigo(),
                e.getDescricao(),
                e.getDataCadastro(),
                e.getAtivo()
        );
    }

    public DominioSistema buscarPorId(Long id) {
        return sistemaDominioRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("Id não encontrado: " + id)
        );
    }

    public DominioSistema statusAberto() {
        return sistemaDominioRepository.findByCategoriaAndCodigoAndAtivoTrue(
                "STATUS_EXERCICIO", "ABERTO"
        ).orElseThrow();
    }

    public DominioSistema statusFechado() {
        return sistemaDominioRepository.findByCategoriaAndCodigoAndAtivoTrue(
                "STATUS_EXERCICIO", "FECHADO"
        ).orElseThrow();
    }
}
