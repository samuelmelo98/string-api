package org.stringtecnologia.string_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.stringtecnologia.string_api.model.entities.DocumentTemplate;
import java.util.Optional;

@Repository
public interface DocumentRepository extends JpaRepository<DocumentTemplate, Long> {

    // Método essencial para buscar o template pelo nome (slug) no serviço
    Optional<DocumentTemplate> findBySlugAndActiveTrue(String slug);

    Optional<DocumentTemplate>
    findFirstBySlugAndActiveTrueOrderByVersionDesc(
            String slug
    );
}

