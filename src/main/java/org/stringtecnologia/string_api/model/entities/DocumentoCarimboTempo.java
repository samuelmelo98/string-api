package org.stringtecnologia.string_api.model.entities;

import jakarta.persistence.Basic;
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
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "TB_DOCUMENTO_CARIMBO_TEMPO",
        indexes = {
                @Index(
                        name = "IDX_CARIMBO_ASSINATURA",
                        columnList = "ASSINATURA_DOCUMENTO_ID"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DocumentoCarimboTempo implements Serializable {

        private static final long serialVersionUID = 1L;

        @Id
        @GeneratedValue(
                strategy = GenerationType.SEQUENCE,
                generator = "DOCUMENTO_CARIMBO_TEMPO_SEQ"
        )
        @SequenceGenerator(
                name = "DOCUMENTO_CARIMBO_TEMPO_SEQ",
                sequenceName = "SEQ_DOCUMENTO_CARIMBO_TEMPO",
                allocationSize = 1
        )
        @Column(name = "DOCUMENTO_CARIMBO_TEMPO_ID")
        private Long documentoCarimboTempoId;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(
                name = "ASSINATURA_DOCUMENTO_ID",
                nullable = false
        )
        private AssinaturaDocumento assinatura;

        @Column(name = "DATA_CARIMBO")
        private LocalDateTime dataCarimbo;

        @Column(name = "TSA", length = 255)
        private String tsa;

        @Column(name = "SERIAL_TSA", length = 255)
        private String serialTsa;

        @Column(name = "POLITICA_OID", length = 100)
        private String politicaOid;

        @Column(name = "ALGORITMO_HASH", length = 20)
        private String algoritmoHash;

        @Lob
        @Basic(fetch = FetchType.LAZY)
        @Column(name = "TOKEN_RFC3161")
        private byte[] tokenRfc3161;

        @Column(name = "CARIMBO_VALIDO")
        private Boolean carimboValido;
}