package org.stringtecnologia.string_api.model.entities;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.stringtecnologia.string_api.model.enums.StatusVenda;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "tb_venda",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_venda_numero",
                        columnNames = "numero"
                )
        },
        indexes = {
                @Index(
                        name = "idx_venda_numero",
                        columnList = "numero"
                ),
                @Index(
                        name = "idx_venda_data",
                        columnList = "data_venda"
                ),
                @Index(
                        name = "idx_venda_status",
                        columnList = "status"
                ),
                @Index(
                        name = "idx_venda_cliente",
                        columnList = "cliente_id"
                )
        }
)
@Getter
@Setter
public class Venda {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "seq_tb_venda"
    )
    @SequenceGenerator(
            name = "seq_tb_venda",
            sequenceName = "seq_tb_venda",
            allocationSize = 1
    )
    @Column(name = "venda_id")
    private Long vendaId;

    @Column(
            name = "numero",
            nullable = false,
            length = 30
    )
    private String numero;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "cliente_id",
            foreignKey = @ForeignKey(
                    name = "fk_venda_cliente"
            )
    )
    private Cliente cliente;

    @Column(
            name = "data_venda",
            nullable = false
    )
    private LocalDateTime dataVenda;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "status",
            nullable = false,
            length = 30
    )
    private StatusVenda status;

    @Column(
            name = "valor_subtotal",
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal valorSubtotal = BigDecimal.ZERO;

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
    private BigDecimal valorTotal = BigDecimal.ZERO;

    @Column(
            name = "observacao",
            length = 2000
    )
    private String observacao;

    @OneToMany(
            mappedBy = "venda",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @OrderBy("vendaItemId ASC")
    private List<VendaItem> itens = new ArrayList<>();

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

    public void adicionarItem(VendaItem item) {

        item.setVenda(this);
        this.itens.add(item);

    }

    public void removerItem(VendaItem item) {

        this.itens.remove(item);
        item.setVenda(null);

    }

    public void limparItens() {

        this.itens.forEach(
                item -> item.setVenda(null)
        );

        this.itens.clear();

    }

}
