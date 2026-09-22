package org.stringtecnologia.string_api.model.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "tb_ordem_servico",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_ordem_servico_numero",
                        columnNames = "numero"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class OrdemServico implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(
            name = "ordem_servico_seq",
            sequenceName = "seq_tb_ordem_servico",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "ordem_servico_seq"
    )
    @Column(name = "ordem_servico_id")
    private Long ordemServicoId;

    @Column(
            name = "numero",
            nullable = false,
            length = 30
    )
    private String numero;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "cliente_id",
            nullable = false
    )
    private Cliente cliente;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "aparelho_id",
            nullable = false
    )
    private Aparelho aparelho;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "tecnico_responsavel_id",
            foreignKey = @ForeignKey(
                    name = "fk_ordem_servico_tecnico"
            )
    )
    private User tecnicoResponsavel;


    @Column(
            name = "data_atribuicao_tecnico"
    )
    private LocalDateTime dataAtribuicaoTecnico;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "tipo_status_ordem_servico_id",
            nullable = false
    )
    private DominioSistema status;

    @Column(
            name = "defeito_relatado",
            columnDefinition = "TEXT"
    )
    private String defeitoRelatado;

    @Column(
            name = "diagnostico",
            columnDefinition = "TEXT"
    )
    private String diagnostico;

    @Column(
            name = "solucao",
            columnDefinition = "TEXT"
    )
    private String solucao;

    @Column(
            name = "observacao",
            columnDefinition = "TEXT"
    )
    private String observacao;

    @Column(
            name = "valor_orcamento",
            precision = 15,
            scale = 2
    )
    private BigDecimal valorOrcamento;

    @Column(
            name = "valor_final",
            precision = 15,
            scale = 2
    )
    private BigDecimal valorFinal;

    @CreationTimestamp
    @Column(
            name = "data_abertura",
            nullable = false,
            updatable = false
    )
    private LocalDateTime dataAbertura;

    @UpdateTimestamp
    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;

    @Column(name = "data_aprovacao")
    private LocalDateTime dataAprovacao;

    @Column(name = "data_inicio_servico")
    private LocalDateTime dataInicioServico;

    @Column(name = "data_conclusao")
    private LocalDateTime dataConclusao;

    @Column(name = "data_entrega")
    private LocalDateTime dataEntrega;

    @Column(
            name = "consulta_token",
            nullable = false,
            unique = true,
            updatable = false,
            length = 36
    )
    private String consultaToken;


}