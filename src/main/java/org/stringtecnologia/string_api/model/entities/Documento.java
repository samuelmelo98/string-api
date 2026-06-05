package org.stringtecnologia.string_api.model.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "TB_DOCUMENTO")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Documento implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DOCUMENTO_SEQ")
    @SequenceGenerator(name = "DOCUMENTO_SEQ", sequenceName = "SEQ_DOCUMENTO", allocationSize = 1)
    @Column(name = "ID")
    private Long documentoId;

    private String nome;
    @Column(name = "MIME_TYPE")
    private String mimeType;
    private Long tamanho;
    private String caminho;
    private String descricao;
    private LocalDateTime dataCadastro;

    private String hashSha256;

    @OneToMany(
            mappedBy = "documento",
            cascade = CascadeType.ALL
    )
    private List<AssinaturaDocumento> assinaturas;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TIPO_DOCUMENTO_ID", nullable = false)
    private DominioSistema tipoDocumento;

    @Column(name = "ALGORITMO_HASH", length = 20)
    private String algoritmoHash;


    @PrePersist
    public void prePersist() {
        this.dataCadastro = LocalDateTime.now();
    }
}
