package org.stringtecnologia.string_api.model.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.cache.annotation.Cacheable;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "TB_DOCUMENTO_TEMPLATE",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_document_slug_version",
                        columnNames = {"slug", "version"}
                )
        })
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Cacheable(
        value = "documentos-template",
        key = "#slug"
)
public class DocumentoTemplate implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DOCUMENTO_TEMPLATE_SEQ")
    @SequenceGenerator(name = "DOCUMENTO_TEMPLATE_SEQ", sequenceName = "SEQ_DOCUMENTO_TEMPLATE", allocationSize = 1)
    @Column(name = "DOCUMENTO_TEMPLATE_ID")
    private Long documentoTemplateId;


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

