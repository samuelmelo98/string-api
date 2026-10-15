CREATE TABLE tb_ocr_imagem (
 id BIGSERIAL PRIMARY KEY,
 nome_arquivo VARCHAR(255) NOT NULL,
 content_type VARCHAR(80) NOT NULL,
 storage_key VARCHAR(500) NOT NULL UNIQUE,
 sha256 CHAR(64) NOT NULL UNIQUE,
 status VARCHAR(20) NOT NULL DEFAULT 'PENDENTE',
 tentativa INTEGER NOT NULL DEFAULT 0,
 erro TEXT,
 criado_em TIMESTAMPTZ NOT NULL DEFAULT NOW(),
 processado_em TIMESTAMPTZ
);
CREATE INDEX idx_ocr_imagem_status ON tb_ocr_imagem(status, criado_em);
CREATE TABLE tb_ocr_codigo (
 id BIGSERIAL PRIMARY KEY,
 imagem_id BIGINT NOT NULL REFERENCES tb_ocr_imagem(id) ON DELETE CASCADE,
 valor VARCHAR(255) NOT NULL,
 normalizado VARCHAR(255) NOT NULL,
 origem VARCHAR(15) NOT NULL,
 CONSTRAINT uq_ocr_imagem_codigo UNIQUE(imagem_id, normalizado)
);
CREATE INDEX idx_ocr_codigo_normalizado ON tb_ocr_codigo(normalizado, imagem_id);
