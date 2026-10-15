package org.stringtecnologia.string_api.ocr.model;

import jakarta.persistence.*;

@Entity
@Table(name = "tb_ocr_codigo")
public class OcrCodigo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "imagem_id")
    public OcrImagem imagem;
    @Column(nullable = false, length = 255)
    public String valor;
    @Column(nullable = false, length = 255)
    public String normalizado;
    @Column(nullable = false, length = 15)
    public String origem;

    public OcrCodigo() {
    }

    public OcrCodigo(OcrImagem imagem, String valor, String normalizado, String origem) {
        this.imagem = imagem;
        this.valor = valor;
        this.normalizado = normalizado;
        this.origem = origem;
    }
}
