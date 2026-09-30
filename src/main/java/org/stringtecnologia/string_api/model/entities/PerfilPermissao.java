package org.stringtecnologia.string_api.model.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tb_perfil_permissao")
@Getter
@Setter
@NoArgsConstructor
public class PerfilPermissao {

    @EmbeddedId
    private PerfilPermissaoId id;

    @MapsId("perfilId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "perfil_id",
            nullable = false
    )
    private Perfil perfil;

    @MapsId("permissaoId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "permissao_id",
            nullable = false
    )
    private Permissao permissao;
}