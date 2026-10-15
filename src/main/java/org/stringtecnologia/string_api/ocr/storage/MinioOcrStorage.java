package org.stringtecnologia.string_api.ocr.storage;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@Component
@ConditionalOnProperty(
        name = "ocr.storage.provider",
        havingValue = "minio"
)
public class MinioOcrStorage implements OcrStorage {

    private final MinioClient minioClient;
    private final String bucket;
    private final String prefix;

    public MinioOcrStorage(
            MinioClient minioClient,
            @Value("${minio.bucket}") String bucket,
            @Value("${ocr.storage.prefix:ocr/imagens}") String prefix) {

        this.minioClient = minioClient;
        this.bucket = bucket;
        this.prefix = prefix;
    }

    @Override
    public void salvar(String key, byte[] content) throws IOException {
        try (var input = new ByteArrayInputStream(content)) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectKey(key))
                            .stream(input, content.length, -1)
                            .contentType(contentType(key))
                            .build()
            );
        } catch (Exception e) {
            throw new IOException("Erro ao salvar imagem no MinIO", e);
        }
    }

    @Override
    public byte[] ler(String key) throws IOException {
        try (var input = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucket)
                        .object(objectKey(key))
                        .build()
        )) {
            return input.readAllBytes();
        } catch (Exception e) {
            throw new IOException("Erro ao ler imagem do MinIO", e);
        }
    }

    private String objectKey(String key) {
        if (key == null ||
                !key.matches("[a-zA-Z0-9_-]+\\.(png|jpg|jpeg)")) {
            throw new IllegalArgumentException("Chave de imagem inválida");
        }

        return prefix.replaceAll("/+$", "") + "/" + key;
    }

    private String contentType(String key) {
        return key.endsWith(".png")
                ? "image/png"
                : "image/jpeg";
    }
}
