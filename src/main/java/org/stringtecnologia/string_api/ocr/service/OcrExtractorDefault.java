
package org.stringtecnologia.string_api.ocr.service;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.multi.GenericMultipleBarcodeReader;
import net.sourceforge.tess4j.Tesseract;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

@Component
@ConditionalOnProperty(
        name = "ocr.engine",
        havingValue = "tesseract",
        matchIfMissing = true
)
public class OcrExtractorDefault implements OcrExtractor {

    private static final Logger log =
            LoggerFactory.getLogger(OcrExtractorDefault.class);

    private static final Pattern TOKENS = Pattern.compile(
            "[\\p{L}\\p{N}][\\p{L}\\p{N}._/-]{2,254}"
    );

    private final String dataPath;
    private final boolean segundaPassagem;

    public OcrExtractorDefault(
            @Value("${ocr.tessdata.path:./tessdata}") String dataPath,
            @Value("${ocr.segunda-passagem:true}") boolean segundaPassagem
    ) {
        this.dataPath = dataPath;
        this.segundaPassagem = segundaPassagem;
    }

    @Override
    public List<Achado> extrair(byte[] bytes) throws Exception {

        BufferedImage imagem = ImageIO.read(
                new ByteArrayInputStream(bytes)
        );

        if (imagem == null) {
            throw new IllegalArgumentException("Imagem inválida");
        }

        long inicio = System.nanoTime();

        Map<String, Achado> encontrados = new LinkedHashMap<>();

        // 1. Identifica códigos de barras e QR Codes.
        extrairCodigosBarras(imagem, encontrados);

        // 2. OCR na orientação original.
        long inicioOcr = System.nanoTime();

        String textoOriginal = executarOcr(imagem, 3);
        extrairTokens(textoOriginal, encontrados);

        log.info(
                "OCR original (0°): {} ms",
                tempoMs(inicioOcr)
        );

        // 3. Segunda passagem com ampliação e escala de cinza.
        if (segundaPassagem) {

            long inicioAlternativo = System.nanoTime();

            BufferedImage melhorada = melhorarImagem(imagem);

            String textoAlternativo = executarOcr(melhorada, 6);
            extrairTokens(textoAlternativo, encontrados);

            log.info(
                    "OCR segunda passagem (0°): {} ms",
                    tempoMs(inicioAlternativo)
            );
        }

        // 4. OCR nas demais orientações.
        for (int graus : new int[]{90, 180, 270}) {

            long inicioRotacao = System.nanoTime();

            BufferedImage rotacionada = rotacionar(imagem, graus);

            String textoRotacionado = executarOcr(rotacionada, 6);

            extrairTokens(textoRotacionado, encontrados);

            log.info(
                    "OCR rotacionado ({}°): {} ms",
                    graus,
                    tempoMs(inicioRotacao)
            );
        }

        log.info(
                "Extração concluída: {} identificadores em {} ms",
                encontrados.size(),
                tempoMs(inicio)
        );

        return new ArrayList<>(encontrados.values());
    }
    private void extrairCodigosBarras(
            BufferedImage imagem,
            Map<String, Achado> encontrados
    ) {

        try {

            Map<DecodeHintType, Object> hints = new EnumMap<>(
                    DecodeHintType.class
            );

            hints.put(DecodeHintType.TRY_HARDER, true);

            MultiFormatReader reader = new MultiFormatReader();
            reader.setHints(hints);

            BinaryBitmap bitmap = new BinaryBitmap(
                    new HybridBinarizer(
                            new BufferedImageLuminanceSource(imagem)
                    )
            );

            Result[] resultados =
                    new GenericMultipleBarcodeReader(reader)
                            .decodeMultiple(bitmap);

            for (Result resultado : resultados) {
                adicionar(
                        resultado.getText(),
                        "BARCODE",
                        encontrados
                );
            }

        } catch (NotFoundException ignored) {
            log.debug("Nenhum código de barras encontrado");
        }
    }

    private String executarOcr(
            BufferedImage imagem,
            int psm
    ) throws Exception {

        Tesseract tess = new Tesseract();

        tess.setDatapath(dataPath);
        tess.setLanguage("por+eng");
        tess.setPageSegMode(psm);

        return tess.doOCR(imagem);
    }

    private BufferedImage melhorarImagem(
            BufferedImage original
    ) {

        int largura = original.getWidth() * 2;
        int altura = original.getHeight() * 2;

        BufferedImage resultado = new BufferedImage(
                largura,
                altura,
                BufferedImage.TYPE_BYTE_GRAY
        );

        Graphics2D g = resultado.createGraphics();

        try {

            g.setRenderingHint(
                    RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BICUBIC
            );

            g.drawImage(
                    original,
                    0,
                    0,
                    largura,
                    altura,
                    null
            );

        } finally {
            g.dispose();
        }

        return resultado;
    }

    private void extrairTokens(
            String texto,
            Map<String, Achado> encontrados
    ) {

        Matcher matcher = TOKENS.matcher(texto);

        while (matcher.find()) {
            adicionar(
                    matcher.group(),
                    "OCR",
                    encontrados
            );
        }
    }

    private void adicionar(
            String valor,
            String origem,
            Map<String, Achado> encontrados
    ) {

        if (valor == null || valor.isBlank()) {
            return;
        }

        String chave = origem + ":" + valor.toUpperCase(Locale.ROOT);

        encontrados.putIfAbsent(
                chave,
                new Achado(valor, origem)
        );
    }

    private long tempoMs(long inicio) {
        return (System.nanoTime() - inicio) / 1_000_000;
    }

    private BufferedImage rotacionar(
            BufferedImage origem,
            int graus
    ) {
        int largura = origem.getWidth();
        int altura = origem.getHeight();

        boolean trocarDimensoes = graus == 90 || graus == 270;

        int novaLargura = trocarDimensoes ? altura : largura;
        int novaAltura = trocarDimensoes ? largura : altura;

        BufferedImage destino = new BufferedImage(
                novaLargura,
                novaAltura,
                BufferedImage.TYPE_INT_RGB
        );

        Graphics2D g = destino.createGraphics();

        try {
            g.translate(novaLargura / 2.0, novaAltura / 2.0);
            g.rotate(Math.toRadians(graus));
            g.translate(-largura / 2.0, -altura / 2.0);
            g.drawImage(origem, 0, 0, null);
        } finally {
            g.dispose();
        }

        return destino;
    }
}
