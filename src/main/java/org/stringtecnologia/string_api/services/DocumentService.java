package org.stringtecnologia.string_api.services;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.stringtecnologia.string_api.model.entities.DocumentProcessor;
import org.stringtecnologia.string_api.model.entities.DocumentTemplate;
import org.stringtecnologia.string_api.repository.DocumentRepository;

import java.util.Map;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final DocumentProcessor processor;

    public DocumentService(
            DocumentRepository documentRepository,
            @Qualifier("mustacheDocumentProcessor")
            DocumentProcessor processor
    ) {
        this.documentRepository = documentRepository;
        this.processor = processor;
    }

    public String generateDocument(
            String slug,
            Map<String, Object> data
    ) {

        DocumentTemplate template =
                documentRepository
                        .findFirstBySlugAndActiveTrueOrderByVersionDesc(
                                slug
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Template não encontrado: "
                                                + slug
                                )
                        );

        return processor.process(
                template.getTemplate(),
                data
        );
    }
}