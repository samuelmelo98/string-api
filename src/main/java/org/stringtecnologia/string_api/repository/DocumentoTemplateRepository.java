package org.stringtecnologia.string_api.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.stringtecnologia.string_api.model.entities.DocumentoTemplate;

import java.util.Optional;

public interface DocumentoTemplateRepository extends JpaRepository<DocumentoTemplate, Long> {

    Optional<DocumentoTemplate> findBySlugAndActiveTrue(String slug);

    Optional<DocumentoTemplate>
    findFirstBySlugAndActiveTrueOrderByVersionDesc(
            String slug
    );
}
