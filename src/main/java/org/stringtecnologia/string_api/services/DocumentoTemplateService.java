package org.stringtecnologia.string_api.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.stringtecnologia.string_api.documento.dto.AdiantamentoDocumentoDTO;
import org.stringtecnologia.string_api.documento.dto.OrdemServicoDocumentoDTO;
import org.stringtecnologia.string_api.model.entities.DocumentoTemplate;
import org.stringtecnologia.string_api.model.interfaces.DocumentoProcessor;
import org.stringtecnologia.string_api.repository.DocumentoTemplateRepository;

@Service
@RequiredArgsConstructor
public class DocumentoTemplateService {

    private final DocumentoTemplateRepository documentoTemplateRepository;
    private final DocumentoProcessor documentoProcessor;

    public String gerarAdiantamento(
            String slug,
            AdiantamentoDocumentoDTO dto
    ) {

        DocumentoTemplate template =
                documentoTemplateRepository
                        .findFirstBySlugAndActiveTrueOrderByVersionDesc(slug)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Template não encontrado"
                                )
                        );

        return documentoProcessor.process(
                template.getTemplate(),
                dto
        );
    }

    public String gerarOrdemServico(
            String slug,
            OrdemServicoDocumentoDTO dto
    ) {

        DocumentoTemplate template =
                documentoTemplateRepository
                        .findFirstBySlugAndActiveTrueOrderByVersionDesc(slug)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Template não encontrado"
                                )
                        );

        return documentoProcessor.process(
                template.getTemplate(),
                dto
        );
    }

    public String gerar(
            String slug,
            Object dto
    ) {

        DocumentoTemplate template =
                documentoTemplateRepository
                        .findFirstBySlugAndActiveTrueOrderByVersionDesc(slug)
                        .orElseThrow(() ->
                                new RuntimeException("Template não encontrado")
                        );

        return documentoProcessor.process(
                template.getTemplate(),
                dto
        );
    }
}
