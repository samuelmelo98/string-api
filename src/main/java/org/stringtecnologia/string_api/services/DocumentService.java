package org.stringtecnologia.string_api.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.stringtecnologia.string_api.model.entities.DocumentTemplate;
import org.stringtecnologia.string_api.repository.DocumentRepository;
import org.stringtecnologia.string_api.model.entities.DocumentProcessor; // Ou onde estiver sua interface
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final DocumentProcessor processor;

    public String generateDocument(String slug, Map<String, Object> data) {
        // Trocamos ResourceNotFoundException por RuntimeException padrão
        DocumentTemplate template = documentRepository.findFirstBySlugAndActiveTrueOrderByVersionDesc(slug)
                .orElseThrow(() -> new RuntimeException("Template não encontrado: " + slug));

        // Ajustado de getContent() para getTemplate() para bater com sua Entity
        return processor.process(template.getTemplate(), data);
    }
}