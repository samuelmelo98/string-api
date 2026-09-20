package org.stringtecnologia.string_api.model.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.stringtecnologia.string_api.model.enums.StatusOrcamento;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "tb_ordem_servico_orcamento",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_ordem_servico_orcamento_versao",
                        columnNames = {
                                "ordem_servico_id",
                                "versao"
                        }
                )
        }
)
@Getter
@Setter
public class OrdemServicoOrcamento {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "seq_ordem_servico_orcamento"
    )
    @SequenceGenerator(
            name = "seq_ordem_servico_orcamento",
            sequenceName = "seq_tb_ordem_servico_orcamento",
            allocationSize = 1
    )
    @Column(
            name = "ordem_servico_orcamento_id",
            nullable = false
    )
    private Long ordemServicoOrcamentoId;


    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "ordem_servico_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_orcamento_ordem_servico"
            )
    )
    private OrdemServico ordemServico;


    @Column(
            name = "versao",
            nullable = false
    )
    private Integer versao;


    @Column(
            name = "servico_proposto",
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String servicoProposto;


    @Column(
            name = "valor_mao_obra",
            precision = 15,
            scale = 2
    )
    private BigDecimal valorMaoObra;


    @Column(
            name = "valor_pecas",
            precision = 15,
            scale = 2
    )
    private BigDecimal valorPecas;


    @Column(
            name = "desconto",
            precision = 15,
            scale = 2
    )
    private BigDecimal desconto;


    @Column(
            name = "valor_total",
            nullable = false,
            precision = 15,
            scale = 2
    )
    private BigDecimal valorTotal;


    @Column(
            name = "observacao",
            columnDefinition = "TEXT"
    )
    private String observacao;


    @Enumerated(EnumType.STRING)
    @Column(
            name = "status",
            nullable = false,
            length = 30
    )
    private StatusOrcamento status;


    @CreationTimestamp
    @Column(
            name = "data_criacao",
            nullable = false,
            updatable = false
    )
    private LocalDateTime dataCriacao;


    @UpdateTimestamp
    @Column(
            name = "data_atualizacao"
    )
    private LocalDateTime dataAtualizacao;


    @Column(
            name = "data_envio"
    )
    private LocalDateTime dataEnvio;


    @Column(
            name = "data_aprovacao"
    )
    private LocalDateTime dataAprovacao;


    @Column(
            name = "data_reprovacao"
    )
    private LocalDateTime dataReprovacao;
}