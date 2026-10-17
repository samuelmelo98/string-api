
package org.stringtecnologia.string_api.ocr.service;

import org.springframework.stereotype.Service;
import org.stringtecnologia.string_api.ocr.model.OcrImagem;
import org.stringtecnologia.string_api.ocr.model.OcrStatus;
import org.stringtecnologia.string_api.ocr.repository.OcrImagemRepository;
import org.stringtecnologia.string_api.ocr.storage.OcrStorage;

@Service
public class OcrProcessamentoServiceImpl implements OcrProcessamentoService {

    private final OcrImagemRepository repo;
    private final OcrIndexador indexador;
    private final OcrExtractor extractor;
    private final OcrStorage storage;

    public OcrProcessamentoServiceImpl(
            OcrImagemRepository repo,
            OcrIndexador indexador,
            OcrExtractor extractor,
            OcrStorage storage
    ) {
        this.repo = repo;
        this.indexador = indexador;
        this.extractor = extractor;
        this.storage = storage;
    }

    @Override
    public void processarPorId(Long imagemId) {

        if (!indexador.iniciar(imagemId)) {
            validarEstado(imagemId);
            return;
        }

        try {
            OcrImagem imagem = repo.findById(imagemId)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Imagem OCR não encontrada: " + imagemId
                    ));

            var resultado = extractor.extrair(
                    storage.ler(imagem.storageKey)
            );

            indexador.sucesso(imagemId, resultado);

        } catch (Exception e) {
            try {
                indexador.prepararNovaTentativa(imagemId, e);
            } catch (Exception recuperacaoErro) {
                e.addSuppressed(recuperacaoErro);
            }

            throw new IllegalStateException(
                    "Falha no processamento OCR da imagem " + imagemId,
                    e
            );
        }
    }

    private void validarEstado(Long imagemId) {

        OcrImagem imagem = repo.findById(imagemId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Imagem OCR não encontrada: " + imagemId
                ));

        if (imagem.status == OcrStatus.PROCESSADO) {
            return;
        }

        throw new IllegalStateException(
                "Imagem OCR não disponível para processamento. id="
                        + imagemId
                        + ", status="
                        + imagem.status
        );
    }
}
