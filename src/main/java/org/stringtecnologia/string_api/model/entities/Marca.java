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
@Table(name = "tb_marca")
public class Marca implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Setter(AccessLevel.NONE)
    @SequenceGenerator(
            name = "marca_seq",
            sequenceName = "seq_tb_marca",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "marca_seq"
    )
    @Column(name = "marca_id")
    private Long marcaId;

    @Column(name = "nome", nullable = false, length = 100)
    private String nome;

    @Column(name = "ativo", nullable = false)
    private boolean ativo = true;
}