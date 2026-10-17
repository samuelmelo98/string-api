package org.stringtecnologia.string_api.ocr.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.stringtecnologia.string_api.ocr.model.OcrCodigo;
import org.stringtecnologia.string_api.ocr.model.OcrImagem;
import org.stringtecnologia.string_api.ocr.model.OcrStatus;
import org.stringtecnologia.string_api.ocr.repository.OcrCodigoRepository;
import org.stringtecnologia.string_api.ocr.repository.OcrImagemRepository;

import java.time.Instant;
import java.util.*;


@Service
public class OcrIndexador {
    private final OcrImagemRepository imagens;
    private final OcrCodigoRepository codigos;

    public OcrIndexador(OcrImagemRepository imagens, OcrCodigoRepository codigos) {
        this.imagens = imagens;
        this.codigos = codigos;
    }

    @Transactional
    public boolean iniciar(Long id) {
        return imagens.iniciarProcessamento(
                id,
                OcrStatus.PENDENTE,
                OcrStatus.PROCESSANDO
        ) == 1;
    }

    @Transactional
    public void sucesso(Long id, List<OcrExtractor.Achado> achados) {
        OcrImagem imagem = imagens.findById(id).orElseThrow();

        if (imagem.status != OcrStatus.PROCESSANDO) {
            throw new IllegalStateException(
                    "Imagem não está em processamento: " + id
            );
        }

        codigos.deleteByImagemId(id);
        codigos.flush();

        Set<String> vistos = new HashSet<>();

        for (var a : achados) {
            String n = OcrNormalizer.normalizar(a.valor());

            if (n.length() < 3 || n.length() > 255 || !vistos.add(n)) {
                continue;
            }

            codigos.save(new OcrCodigo(imagem, a.valor(), n, a.origem()));
        }

        imagem.status = OcrStatus.PROCESSADO;
        imagem.processadoEm = Instant.now();
        imagem.erro = null;
    }

    @Transactional
    public void falha(Long id, Exception ex) {

        OcrImagem imagem = imagens.findById(id)
                .orElseThrow();

        if (imagem.status != OcrStatus.PENDENTE
                && imagem.status != OcrStatus.PROCESSANDO) {
            throw new IllegalStateException(
                    "Não é possível registrar falha no status: "
                            + imagem.status
            );
        }

        imagem.status = OcrStatus.ERRO;
        imagem.erro = resumirErro(ex);
    }

    @Transactional
    public void reprocessar(Long id) {
        OcrImagem imagem = imagens.findById(id).orElseThrow();
        if (imagem.status == OcrStatus.PROCESSANDO) throw new IllegalStateException("Em processamento");
        imagem.status = OcrStatus.PENDENTE;
        imagem.erro = null;
    }

    @Transactional
    public void prepararNovaTentativa(Long id, Exception ex) {
        OcrImagem imagem = imagens.findById(id).orElseThrow();

        if (imagem.status != OcrStatus.PROCESSANDO) {
            throw new IllegalStateException(
                    "Imagem não está em processamento: " + id
            );
        }

        imagem.status = OcrStatus.PENDENTE;
        imagem.erro = resumirErro(ex);
    }

    private String resumirErro(Exception ex) {
        String mensagem = ex.getClass().getSimpleName()
                + ": " + ex.getMessage();

        return mensagem.substring(0, Math.min(500, mensagem.length()));
    }
}
