package org.stringtecnologia.string_api.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.stringtecnologia.string_api.model.entities.DocumentoTemplate;
import org.stringtecnologia.string_api.model.interfaces.DocumentoProcessor;
import org.stringtecnologia.string_api.model.interfaces.DocumentoProcessorGenerico;
import org.stringtecnologia.string_api.repository.DocumentoTemplateRepository;

@Service
@RequiredArgsConstructor
public class DocumentoTemplateGenericoService {


        private final DocumentoTemplateRepository repository;
        private final DocumentoProcessorGenerico documentoProcessorGenerico;

        public <T> String gerar(
                String slug,
                T dto
        ) {

            DocumentoTemplate template =
                    repository
                            .findFirstBySlugAndActiveTrueOrderByVersionDesc(slug)
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "Template não encontrado"
                                    )
                            );

            return documentoProcessorGenerico.process(
                    template.getTemplate(),
                    dto
            );
        }
    }
