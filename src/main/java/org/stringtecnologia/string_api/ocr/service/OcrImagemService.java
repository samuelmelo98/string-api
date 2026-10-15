package org.stringtecnologia.string_api.ocr.service;

import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.stringtecnologia.string_api.ocr.dto.ImagemDto;
import org.stringtecnologia.string_api.ocr.model.OcrImagem;
import org.stringtecnologia.string_api.ocr.repository.OcrCodigoRepository;
import org.stringtecnologia.string_api.ocr.repository.OcrImagemRepository;
import org.stringtecnologia.string_api.ocr.storage.OcrStorage;

import java.io.*;
import java.security.*;
import java.util.*;


@Service
public class OcrImagemService {
    private final OcrImagemRepository imagens;
    private final OcrCodigoRepository codigos;
    private final OcrStorage storage;
    private final OcrIndexador indexador;

    public OcrImagemService(OcrImagemRepository imagens, OcrCodigoRepository codigos, OcrStorage storage, OcrIndexador indexador) {
        this.imagens = imagens;
        this.codigos = codigos;
        this.storage = storage;
        this.indexador = indexador;
    }

    public ImagemDto upload(MultipartFile file) throws Exception {
        if (file.isEmpty() || file.getSize() > 15_000_000)
            throw new IllegalArgumentException("Imagem vazia ou maior que 15MB");
        byte[] bytes = file.getBytes();
        var img = javax.imageio.ImageIO.read(new ByteArrayInputStream(bytes));
        if (img == null || ((long) img.getWidth() * img.getHeight()) > 40_000_000L)
            throw new IllegalArgumentException("Imagem invalida ou dimensoes excessivas");
        String hash = HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(bytes));
        var existente = imagens.findBySha256(hash);
        if (existente.isPresent()) return ImagemDto.from(existente.get());
        OcrImagem i = new OcrImagem();
        i.sha256 = hash;
        i.nomeArquivo = Optional.ofNullable(file.getOriginalFilename()).orElse("imagem");
        i.contentType = switch (img.getColorModel().hasAlpha() ? "alpha" : "opaque") {
            default -> "image/png";
        };
        // Mantem o binario original; identifica o formato por assinatura via ImageIO.
        try (var in = javax.imageio.ImageIO.createImageInputStream(new ByteArrayInputStream(bytes))) {
            var readers = javax.imageio.ImageIO.getImageReaders(in);
            if (!readers.hasNext()) throw new IllegalArgumentException("Formato nao suportado");
            String fmt = readers.next().getFormatName().toLowerCase(Locale.ROOT);
            if (!Set.of("png", "jpeg", "jpg").contains(fmt)) throw new IllegalArgumentException("Somente PNG/JPEG");
            i.contentType = fmt.equals("png") ? "image/png" : "image/jpeg";
        }
        i.storageKey = UUID.randomUUID() + (i.contentType.equals("image/png") ? ".png" : ".jpg");
        storage.salvar(i.storageKey, bytes);
        return ImagemDto.from(imagens.save(i));
    }

    public ImagemDto detalhe(Long id) {
        return ImagemDto.from(imagens.findById(id).orElseThrow());
    }

    public byte[] arquivo(Long id) throws IOException {
        return storage.ler(imagens.findById(id).orElseThrow().storageKey);
    }

    public String contentType(Long id) {
        return imagens.findById(id).orElseThrow().contentType;
    }

    public Page<ImagemDto> buscar(String codigo, Pageable pageable) {
        String n = OcrNormalizer.normalizar(codigo);
        if (n.length() < 3) throw new IllegalArgumentException("Codigo muito curto");
        return codigos.buscarPorCodigo(n, pageable).map(ImagemDto::from);
    }

    public ImagemDto reprocessar(Long id) {
        indexador.reprocessar(id);
        return detalhe(id);
    }
}
