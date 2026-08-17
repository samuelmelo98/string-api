package org.stringtecnologia.string_api.model.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "TB_ARQUIVO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Arquivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private UUID uuid;

    @Column(nullable = false)
    private String bucket;

    @Column(nullable = false, unique = true)
    private String objectKey;

    @Column(nullable = false)
    private String nomeOriginal;

    private String contentType;

    @Column(nullable = false)
    private Long tamanho;

    private String etag;

    private String sha256;

    private LocalDateTime dataUpload;

    private LocalDateTime dataExclusao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USUARIO_UPLOAD_ID")
    private User usuarioUpload;
}
