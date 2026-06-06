package org.stringtecnologia.string_api.model.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.stringtecnologia.string_api.model.enums.Marca;
import org.stringtecnologia.string_api.model.enums.TipoAparalho;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter

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
    private Marca marca;
    private String modelo;
    private String modeloComercial;
    private String numeroSerie;
    private String descricao;
    private TipoAparalho tipo;
    private String defeito;
    private String observacao;
    private LocalDateTime dataCadastro;
    private LocalDateTime dataAtualizacao;
    private LocalDateTime dataSaida;
    private LocalDateTime fimGarantia;

}
