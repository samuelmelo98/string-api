package org.stringtecnologia.string_api.model.entities;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "tb_tipo_aparelho")
public class TipoAparelho implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Setter(AccessLevel.NONE)
    @SequenceGenerator(
            name = "tipo_aparelho_seq",
            sequenceName = "seq_tb_tipo_aparelho",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "tipo_aparelho_seq"
    )
    @Column(name = "tipo_aparelho_id")
    private Long tipoAparelhoId;

    @Column(name = "nome", nullable = false, length = 100)
    private String nome;

    @Column(name = "ativo", nullable = false)
    private boolean ativo = true;
}