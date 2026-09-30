package org.stringtecnologia.string_api.model.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class PerfilPermissaoId implements Serializable {

    @Column(name = "perfil_id")
    private Long perfilId;

    @Column(name = "permissao_id")
    private Long permissaoId;
}