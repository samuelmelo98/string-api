package org.stringtecnologia.string_api.ocr.service;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.stringtecnologia.string_api.ocr.model.OcrImagem;
import org.stringtecnologia.string_api.ocr.model.OcrOutbox;
import org.stringtecnologia.string_api.ocr.repository.OcrImagemRepository;
import org.stringtecnologia.string_api.ocr.repository.OcrOutboxRepository;

@Service
public class OcrCadastroPersistenciaService {

    private final OcrImagemRepository imagens;
    private final OcrOutboxRepository outbox;
    private final String topico;

    public OcrCadastroPersistenciaService(
            OcrImagemRepository imagens,
            OcrOutboxRepository outbox,
            @Value("${ocr.kafka.topic:ocr.imagens.dev}")
            String topico
    ) {
        this.imagens = imagens;
        this.outbox = outbox;
        this.topico = topico;
    }

    @Transactional
    public OcrImagem salvarComEvento(OcrImagem imagem) {

        OcrImagem salva = imagens.saveAndFlush(imagem);

        outbox.save(new OcrOutbox(
                salva.id,
                topico
        ));

        return salva;
    }
}

