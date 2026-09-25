package org.stringtecnologia.string_api.model.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.stringtecnologia.string_api.model.enums.FormaPagamento;
import org.stringtecnologia.string_api.model.enums.StatusPagamento;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "tb_ordem_servico_pagamento",
        indexes = {
                @Index(
                        name = "idx_pagamento_ordem_servico",
                        columnList = "ordem_servico_id"
                ),
                @Index(
                        name = "idx_pagamento_status",
                        columnList = "status"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class OrdemServicoPagamento {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "ordem_servico_pagamento_seq"
    )
    @SequenceGenerator(
            name = "ordem_servico_pagamento_seq",
            sequenceName = "seq_tb_ordem_servico_pagamento",
            allocationSize = 1
    )
    @Column(
            name = "ordem_servico_pagamento_id",
            nullable = false
    )
    private Long ordemServicoPagamentoId;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "ordem_servico_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_pagamento_ordem_servico"
            )
    )
    private OrdemServico ordemServico;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "forma_pagamento",
            nullable = false,
            length = 30
    )
    private FormaPagamento formaPagamento;

    @Column(
            name = "valor",
            nullable = false,
            precision = 15,
            scale = 2
    )
    private BigDecimal valor;

    @Column(
            name = "parcelas",
            nullable = false
    )
    private Integer parcelas = 1;

    @Column(
            name = "observacao",
            columnDefinition = "TEXT"
    )
    private String observacao;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "status",
            nullable = false,
            length = 20
    )
    private StatusPagamento status = StatusPagamento.ATIVO;

    @Column(
            name = "data_pagamento",
            nullable = false
    )
    private LocalDateTime dataPagamento;

    @Column(
            name = "data_cancelamento"
    )
    private LocalDateTime dataCancelamento;

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

    @PrePersist
    public void prePersist() {

        if (status == null) {
            status = StatusPagamento.ATIVO;
        }

        if (parcelas == null) {
            parcelas = 1;
        }

        if (dataPagamento == null) {
            dataPagamento = LocalDateTime.now();
        }
    }

    public void cancelar() {

        this.status = StatusPagamento.CANCELADO;
        this.dataCancelamento = LocalDateTime.now();
    }
}