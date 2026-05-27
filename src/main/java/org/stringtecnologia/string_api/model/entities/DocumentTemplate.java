package org.stringtecnologia.string_api.model.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "document_templates",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_document_slug_version",
                        columnNames = {"slug", "version"}
                )
        }
)
@Getter
@Setter
public class DocumentTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Identificador lógico do documento.
     *
     * Ex:
     * termo-adiantamento
     * ordem-pagamento
     * declaracao-recebimento
     */
    @Column(nullable = false, length = 150)
    private String slug;

    /**
     * Versão do template.
     *
     * Ex:
     * 1
     * 2
     * 3
     */
    @Column(nullable = false)
    private Integer version = 1;

    /**
     * HTML do template.
     */
    @Column(columnDefinition = "TEXT", nullable = false)
    private String template;

    /**
     * Indica se esta é a versão ativa/publicada.
     */
    @Column(nullable = false)
    private Boolean active = false;

    /**
     * Data de criação do template.
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Última atualização.
     */
    private LocalDateTime updatedAt;

    /**
     * Descrição opcional do template.
     */
    @Column(length = 500)
    private String description;

    @PrePersist
    public void prePersist() {

        this.createdAt = LocalDateTime.now();

        if (this.version == null) {
            this.version = 1;
        }

        if (this.active == null) {
            this.active = false;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}