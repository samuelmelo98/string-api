package org.stringtecnologia.string_api.model.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_permissao")
@Getter
@Setter
@NoArgsConstructor
public class Permissao {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "seq_tb_permissao"
    )
    @SequenceGenerator(
            name = "seq_tb_permissao",
            sequenceName = "seq_tb_permissao",
            allocationSize = 1
    )
    @Column(name = "permissao_id")
    private Long permissaoId;

    @Column(
            name = "codigo",
            nullable = false,
            unique = true,
            length = 100
    )
    private String codigo;

    @Column(
            name = "nome",
            nullable = false,
            length = 150
    )
    private String nome;

    @Column(
            name = "modulo",
            nullable = false,
            length = 50
    )
    private String modulo;

    @Column(
            name = "descricao",
            length = 255
    )
    private String descricao;

    @Column(
            name = "ativo",
            nullable = false
    )
    private Boolean ativo = true;

    @CreationTimestamp
    @Column(
            name = "data_criacao",
            nullable = false,
            updatable = false
    )
    private LocalDateTime dataCriacao;

    @UpdateTimestamp
    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;
}