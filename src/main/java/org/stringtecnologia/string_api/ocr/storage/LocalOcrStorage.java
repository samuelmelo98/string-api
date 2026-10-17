package org.stringtecnologia.string_api.ocr.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

@Component
@ConditionalOnProperty(
        prefix = "ocr.storage",
        name = "provider",
        havingValue = "local",
        matchIfMissing = true
)
public class LocalOcrStorage implements OcrStorage {

    private final Path root;

    public LocalOcrStorage(
            @Value("${ocr.storage.path:./data/ocr}") String path
    ) {
        this.root = Path.of(path)
                .toAbsolutePath()
                .normalize();
    }

    private Path target(String key) {

        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException(
                    "Storage key não pode ser vazia"
            );
        }

        if (!key.matches("[a-zA-Z0-9_-]+\\.(png|jpg|jpeg)")) {
            throw new IllegalArgumentException(
                    "Storage key inválida"
            );
        }

        Path target = root.resolve(key).normalize();

        if (!target.startsWith(root)) {
            throw new IllegalArgumentException(
                    "Storage key fora do diretório permitido"
            );
        }

        return target;
    }

    @Override
    public void salvar(String key, byte[] content)
            throws IOException {

        Path destino = target(key);

        Files.createDirectories(root);

        Files.write(
                destino,
                content,
                StandardOpenOption.CREATE_NEW,
                StandardOpenOption.WRITE
        );
    }

    @Override
    public byte[] ler(String key)
            throws IOException {

        return Files.readAllBytes(target(key));
    }
}