package org.stringtecnologia.string_api.model.entities;

import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
@Table(
        name = "TB_ASSINATURA_DOCUMENTO",
        indexes = {
                @Index(
                        name = "IDX_ASSINATURA_DOCUMENTO",
                        columnList = "DOCUMENTO_ID"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AssinaturaDocumento implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "ASSINATURA_DOCUMENTO_SEQ"
    )
    @SequenceGenerator(
            name = "ASSINATURA_DOCUMENTO_SEQ",
            sequenceName = "SEQ_ASSINATURA_DOCUMENTO",
            allocationSize = 1
    )
    @Column(name = "ASSINATURA_DOCUMENTO_ID")
    private Long assinaturaDocumentoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "DOCUMENTO_ID",
            nullable = false
    )
    private Documento documento;

    @Column(name = "HASH_ASSINATURA", length = 64)
    private String hashAssinatura;

    @Column(name = "ALGORITMO_HASH", length = 20)
    private String algoritmoHash;

    @Column(name = "ASSINANTE", length = 255)
    private String assinante;

    @Column(name = "CERTIFICADO_SERIAL", length = 255)
    private String certificadoSerial;

    @Column(name = "DATA_ASSINATURA")
    private LocalDateTime dataAssinatura;

    @Column(name = "ASSINATURA_VALIDA")
    private Boolean assinaturaValida;

    @Column(name = "STATUS", length = 30)
    private String status;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "ASSINATURA_CMS")
    private byte[] assinaturaCms;

    @OneToMany(
            mappedBy = "assinatura",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<DocumentoCarimboTempo> carimbosTempo;
}