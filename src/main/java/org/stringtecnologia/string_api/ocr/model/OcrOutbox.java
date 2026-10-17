package org.stringtecnologia.string_api.ocr.model;


import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "tb_ocr_outbox")
public class OcrOutbox {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "imagem_id", nullable = false)
    private Long imagemId;

    @Column(name = "topico", nullable = false, length = 150)
    private String topico;

    @Column(name = "criado_em", nullable = false)
    private Instant criadoEm = Instant.now();

    @Column(name = "publicado_em")
    private Instant publicadoEm;

    @Column(name = "tentativas", nullable = false)
    private int tentativas;

    @Column(name = "ultimo_erro", length = 1000)
    private String ultimoErro;

    protected OcrOutbox() {
    }

    public OcrOutbox(Long imagemId, String topico) {
        this.imagemId = imagemId;
        this.topico = topico;
    }

    public Long getId() {
        return id;
    }

    public Long getImagemId() {
        return imagemId;
    }

    public String getTopico() {
        return topico;
    }

    public Instant getCriadoEm() {
        return criadoEm;
    }

    public Instant getPublicadoEm() {
        return publicadoEm;
    }

    public int getTentativas() {
        return tentativas;
    }

    public String getUltimoErro() {
        return ultimoErro;
    }

    public void marcarPublicado() {
        this.publicadoEm = Instant.now();
        this.ultimoErro = null;
    }

    public void registrarFalha(Exception erro) {
        this.tentativas++;

        String mensagem = erro.getClass().getSimpleName()
                + ": " + erro.getMessage();

        this.ultimoErro = mensagem.substring(
                0,
                Math.min(1000, mensagem.length())
        );
    }
}
