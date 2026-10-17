
CREATE TABLE IF NOT EXISTS tb_ocr_outbox (
                                             id BIGSERIAL PRIMARY KEY,
                                             imagem_id BIGINT NOT NULL,
                                             topico VARCHAR(150) NOT NULL,
    criado_em TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    publicado_em TIMESTAMPTZ,
    tentativas INTEGER NOT NULL DEFAULT 0,
    ultimo_erro VARCHAR(1000),

    CONSTRAINT fk_ocr_outbox_imagem
    FOREIGN KEY (imagem_id)
    REFERENCES tb_ocr_imagem(id)
    );

CREATE INDEX IF NOT EXISTS idx_ocr_outbox_pendente
    ON tb_ocr_outbox (criado_em, id)
    WHERE publicado_em IS NULL;
