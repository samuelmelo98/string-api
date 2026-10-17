package org.stringtecnologia.string_api.ocr.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@ConditionalOnProperty(name = "ocr.engine", havingValue = "quarkus")
public class OcrExtractorQuarkus implements OcrExtractor {
    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10)).build();
    private final ObjectMapper json = new ObjectMapper();
    private final URI endpoint;
    private final String apiKey;
    private final int timeoutSeconds;

    public OcrExtractorQuarkus(
            @Value("${ocr.quarkus.url:http://localhost:8081/api/v1/ocr/extrair}") String url,
            @Value("${ocr.quarkus.api-key}") String apiKey,
            @Value("${ocr.quarkus.timeout-seconds:240}") int timeoutSeconds) {
        this.endpoint = URI.create(url);
        this.apiKey = apiKey;
        this.timeoutSeconds = timeoutSeconds;
    }

    @Override
    public List<Achado> extrair(byte[] imagem) throws Exception {
        String boundary = "ocr-" + UUID.randomUUID();
        boolean png = imagem.length >= 8 && (imagem[0] & 255) == 137 && imagem[1] == 80 && imagem[2] == 78 && imagem[3] == 71;
        String mime = png ? "image/png" : "image/jpeg";
        String filename = png ? "imagem.png" : "imagem.jpg";
        byte[] header = ("--" + boundary + "\r\n"
                + "Content-Disposition: form-data; name=\"arquivo\"; filename=\"" + filename + "\"\r\n"
                + "Content-Type: " + mime + "\r\n\r\n").getBytes(StandardCharsets.UTF_8);
        byte[] footer = ("\r\n--" + boundary + "--\r\n")
                .getBytes(StandardCharsets.UTF_8);
        ByteArrayOutputStream body = new ByteArrayOutputStream(header.length + imagem.length + footer.length);
        body.write(header);
        body.write(imagem);
        body.write(footer);
        HttpRequest request = HttpRequest.newBuilder(endpoint)
                .timeout(Duration.ofSeconds(timeoutSeconds))
                .header("X-API-Key", apiKey)
                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                .POST(HttpRequest.BodyPublishers.ofByteArray(body.toByteArray()))
                .build();
        HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new IllegalStateException("Motor Quarkus retornou HTTP " + response.statusCode());
        }
        OcrResponse result = json.readValue(response.body(), OcrResponse.class);
        Map<String, Achado> únicos = new LinkedHashMap<>();
        if (result.lines() != null) {
            for (OcrLine line : result.lines()) {
                if (line.text() == null || line.text().isBlank()) continue;
                String key = OcrNormalizer.normalizar(line.text());
                if (!key.isEmpty()) únicos.putIfAbsent(key, new Achado(line.text(), "OCR"));
            }
        }
        return new ArrayList<>(únicos.values());
    }

    public record OcrResponse(String engine, long durationMs, List<OcrLine> lines) {}
    public record OcrLine(String text, String normalized, double confidence, int[][] box, int angle) {}
}
