package org.stringtecnologia.string_api.model.entities;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.cache.annotation.CacheEvict;

import java.io.Serializable;
import java.time.LocalDateTime;


@Entity
@Table(
        name = "TB_DOMINIO_SISTEMA",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "UK_DOMINIO_CATEGORIA_CODIGO",
                        columnNames = {"CATEGORIA", "CODIGO"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@CacheEvict(value = "dominioSistema", allEntries = true)
public class DominioSistema implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_DOMINIO_SISTEMA")
    @SequenceGenerator(
            name = "SEQ_DOMINIO_SISTEMA",
            sequenceName = "SEQ_DOMINIO_SISTEMA",
            allocationSize = 1
    )
    @Column(name = "DOMINIO_SISTEMA_ID")
    private Long dominioSistemaId;

    @Column(name = "CATEGORIA", nullable = false, length = 100)
    private String categoria;

    @Column(name = "CODIGO", nullable = false, length = 100)
    private String codigo;

    @Column(name = "DESCRICAO", nullable = false, length = 255)
    private String descricao;

    @Column(name = "DATA_CADASTRO", nullable = false)
    private LocalDateTime dataCadastro;

    @Column(name = "ATIVO", nullable = false)
    private Boolean ativo;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DominioSistema that)) return false;
        return dominioSistemaId != null && dominioSistemaId.equals(that.dominioSistemaId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
