package org.stringtecnologia.string_api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.stringtecnologia.string_api.model.enums.OrigemNomeSugestao;
import org.stringtecnologia.string_api.model.enums.StatusNomeSugestao;
import org.stringtecnologia.string_api.model.enums.TipoNomeSugestao;

import java.time.Instant;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Table(
        name = "tb_nome_sugestao",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_nome_sugestao_tipo_normalizado",
                        columnNames = {
                                "tipo",
                                "nome_normalizado"
                        }
                )
        }
)
@Getter
@NoArgsConstructor(access = PROTECTED)
public class NomeSugestao {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "seq_nome_sugestao"
    )
    @SequenceGenerator(
            name = "seq_nome_sugestao",
            sequenceName = "seq_tb_nome_sugestao",
            allocationSize = 1
    )
    @Column(
            name = "nome_sugestao_id"
    )
    private Long nomeSugestaoId;

    @Column(
            name = "nome",
            nullable = false,
            length = 120
    )
    private String nome;

    @Column(
            name = "nome_normalizado",
            nullable = false,
            length = 120
    )
    private String nomeNormalizado;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "tipo",
            nullable = false,
            length = 30
    )
    private TipoNomeSugestao tipo;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "origem",
            nullable = false,
            length = 30
    )
    private OrigemNomeSugestao origem;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "status",
            nullable = false,
            length = 20
    )
    private StatusNomeSugestao status;

    @Column(
            name = "frequencia",
            nullable = false
    )
    private Long frequencia;

    @Column(
            name = "data_cadastro",
            nullable = false,
            updatable = false
    )
    private Instant dataCadastro;

    @Column(
            name = "data_atualizacao"
    )
    private Instant dataAtualizacao;

    @Version
    @Column(
            name = "versao_registro",
            nullable = false
    )
    private Long versaoRegistro;

    public static NomeSugestao criar(
            String nome,
            String nomeNormalizado,
            TipoNomeSugestao tipo,
            OrigemNomeSugestao origem
    ) {

        NomeSugestao entidade =
                new NomeSugestao();

        entidade.nome =
                nome;

        entidade.nomeNormalizado =
                nomeNormalizado;

        entidade.tipo =
                tipo;

        entidade.origem =
                origem;

        entidade.status =
                StatusNomeSugestao.ATIVO;

        entidade.frequencia =
                0L;

        return entidade;
    }

    public void ativar() {

        this.status =
                StatusNomeSugestao.ATIVO;
    }

    public void inativar() {

        this.status =
                StatusNomeSugestao.INATIVO;
    }

    public void marcarPendente() {

        this.status =
                StatusNomeSugestao.PENDENTE;
    }

    public void registrarOcorrencia() {

        this.frequencia =
                this.frequencia == null
                        ? 1L
                        : this.frequencia + 1L;
    }

    @PrePersist
    private void prePersist() {

        dataCadastro =
                Instant.now();

        if (status == null) {
            status =
                    StatusNomeSugestao.ATIVO;
        }

        if (frequencia == null) {
            frequencia = 0L;
        }
    }

    @PreUpdate
    private void preUpdate() {

        dataAtualizacao =
                Instant.now();
    }
}
