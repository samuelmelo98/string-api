package org.stringtecnologia.string_api.model.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.stringtecnologia.string_api.model.enums.Marca;
import org.stringtecnologia.string_api.model.enums.TipoAparalho;
import org.stringtecnologia.string_api.util.CategoriaDominio;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tb_aparelho")
public class Aparelho implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(
            name = "aparelho_seq",
            sequenceName = "seq_tb_aparelho",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "aparelho_seq"
    )
    private Long aparelhoId;
    @Enumerated(EnumType.STRING)
    @Column(name = "MARCA", nullable = false)
    private Marca marca;
    private String modelo;
    private String modeloComercial;
    private String numeroSerie;
    private String descricao;
    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO", nullable = false)
    private TipoAparalho tipo;
    private String defeito;
    private String observacao;
    @CreationTimestamp
    @Column(name = "DATA_CADASTRO", nullable = false, updatable = false)
    private LocalDateTime dataCadastro;
    @UpdateTimestamp
    @Column(name = "DATA_ATUALIZACAO")
    private LocalDateTime dataAtualizacao;
    private LocalDateTime dataSaida;
    private LocalDateTime fimGarantia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TIPO_STATUS_APARELHO_ID", nullable = false)
    private DominioSistema statusAparelho;

}
