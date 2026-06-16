package org.stringtecnologia.string_api.model.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_orcamento")
@Getter
@Setter
public class Orcamento {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "orcamento_seq"
    )
    private Long orcamentoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "cliente_id",
            nullable = false
    )
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "aparelho_id",
            nullable = false
    )
    private Aparelho aparelho;

    private BigDecimal valor;

    private String defeito;

    private String observacao;

    private LocalDateTime dataCadastro;

    private LocalDateTime validade;
}
