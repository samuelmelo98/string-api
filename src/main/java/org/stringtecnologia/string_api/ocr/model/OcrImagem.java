package org.stringtecnologia.string_api.ocr.model;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "tb_ocr_imagem")
public class OcrImagem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    @Column(name = "nome_arquivo", nullable = false)
    public String nomeArquivo;
    @Column(name = "content_type", nullable = false)
    public String contentType;
    @Column(name = "storage_key", nullable = false, unique = true)
    public String storageKey;
    @Column(nullable = false, unique = true, length = 64)
    public String sha256;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public OcrStatus status = OcrStatus.PENDENTE;
    @Column(nullable = false)
    public int tentativa = 0;
    @Column(columnDefinition = "text")
    public String erro;
    @Column(name = "criado_em", nullable = false)
    public Instant criadoEm = Instant.now();
    @Column(name = "processado_em")
    public Instant processadoEm;
}
