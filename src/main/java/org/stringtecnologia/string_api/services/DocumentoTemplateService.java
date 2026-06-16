package org.stringtecnologia.string_api.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.stringtecnologia.string_api.documento.dto.AdiantamentoDocumentoDTO;
import org.stringtecnologia.string_api.documento.dto.OrdemServicoDocumentoDTO;
import org.stringtecnologia.string_api.model.entities.DocumentoTemplate;
import org.stringtecnologia.string_api.model.interfaces.DocumentoProcessor;
import org.stringtecnologia.string_api.repository.DocumentoTemplateRepository;

import java.util.HashMap;
import java.util.Map;

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
                //dto
                null
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
              //  dto
                null
        );
    }

    public String gerar(
            String slug,
            Map<String,Object> dados
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
                dados
        );
    }


    public String gerarPreview(
            String template,
            Map<String, Object> dados
    ) {

        return documentoProcessor.process(
                template,
                dados
        );
    }
}
