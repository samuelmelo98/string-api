package org.stringtecnologia.string_api.model.entities;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(
        name = "tb_venda_item",
        indexes = {
                @Index(
                        name = "idx_venda_item_venda",
                        columnList = "venda_id"
                )
        }
)
@Getter
@Setter
public class VendaItem {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "seq_tb_venda_item"
    )
    @SequenceGenerator(
            name = "seq_tb_venda_item",
            sequenceName = "seq_tb_venda_item",
            allocationSize = 1
    )
    @Column(name = "venda_item_id")
    private Long vendaItemId;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "venda_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_venda_item_venda"
            )
    )
    private Venda venda;

    @Column(
            name = "descricao",
            nullable = false,
            length = 255
    )
    private String descricao;

    @Column(
            name = "quantidade",
            nullable = false,
            precision = 10,
            scale = 2
    )
    private BigDecimal quantidade;

    @Column(
            name = "valor_unitario",
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal valorUnitario;

    @Column(
            name = "desconto",
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal desconto = BigDecimal.ZERO;

    @Column(
            name = "valor_total",
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal valorTotal;

}
