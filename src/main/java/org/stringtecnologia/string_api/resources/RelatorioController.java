package org.stringtecnologia.string_api.resources;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;

import java.util.Map;
import java.util.HashMap;
import java.util.List;

import org.stringtecnologia.string_api.services.PdfService;

import org.stringtecnologia.string_api.repository.DocumentoRepository;

import org.stringtecnologia.string_api.model.dto.Documento;


import com.google.zxing.BarcodeFormat;
import com.google.zxing.qrcode.QRCodeWriter; // 🔥 ESSE É O PRINCIPAL
import com.google.zxing.common.BitMatrix;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.client.j2se.MatrixToImageWriter;

import java.io.ByteArrayOutputStream;
import java.util.Base64;

import java.util.UUID;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;

import javax.sql.DataSource;


@RestController
@RequestMapping("/api/relatorios")
public class RelatorioController {

    @Value("${app.frontend-url}")
private String frontendUrl;

    @Autowired
    private PdfService pdfService;

    @Autowired
private DocumentoRepository repository;

  @GetMapping("/pdf")
public ResponseEntity<byte[]> gerarPdf(HttpServletRequest request) throws Exception {

    String codigo = UUID.randomUUID().toString();

    String baseUrl = request.getScheme() + "://" +
                     request.getServerName() + ":" +
                     request.getServerPort();


                     String urlValidacao = frontendUrl + "/validacao/" + codigo;

   // String urlValidacao = baseUrl + "/api/validacao/" + codigo;

    System.out.println("CODIGO: " + codigo);

    Map<String, Object> dados = new HashMap<>();

    String ip = getClientIp(request);


    dados.put("usuario", "Samuel Silva");
    dados.put("data", "03/04/2026");
    dados.put("dataHora", "03/04/2026 15:00");
    dados.put("projeto", "Sistema RH");
    dados.put("resumo", "Relatório gerado com Spring Boot + Thymeleaf");
    dados.put("titulo", "Relatório de Teste");
    dados.put("ip", ip);
    dados.put("total", "R$ 300");

    // ✅ CORRETO
    dados.put("qrcode", gerarQRCode(urlValidacao));
    dados.put("codigo", codigo);
    dados.put("assinatura", null);

    dados.put("itens", List.of(
        Map.of("nome", "Item 1", "descricao", "Descrição 1", "valor", "R$ 100"),
        Map.of("nome", "Item 2", "descricao", "Descrição 2", "valor", "R$ 200")
    ));

    // salvar documento
    Documento doc = new Documento();
    doc.setCodigo(codigo);
    doc.setUsuario("Samuel Silva");
    doc.setData("03/04/2026");

    repository.salvar(doc);

    byte[] pdf = pdfService.gerarPdf(dados);

    System.out.println("X-Forwarded-For: " + request.getHeader("X-Forwarded-For"));
System.out.println("X-Real-IP: " + request.getHeader("X-Real-IP"));
System.out.println("RemoteAddr: " + request.getRemoteAddr());

    return ResponseEntity.ok()
            .header("Content-Disposition", "attachment; filename=relatorio.pdf")
            .contentType(MediaType.APPLICATION_PDF)
            .body(pdf);
}

    public String gerarQRCode(String texto) throws Exception {
    QRCodeWriter writer = new QRCodeWriter();
    BitMatrix matrix = writer.encode(texto, BarcodeFormat.QR_CODE, 200, 200);

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    MatrixToImageWriter.writeToStream(matrix, "PNG", baos);

    return Base64.getEncoder().encodeToString(baos.toByteArray());
}

private String getClientIp(HttpServletRequest request) {

    String xForwardedFor = request.getHeader("X-Forwarded-For");

    if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
        return xForwardedFor.split(",")[0];
    }

    String realIp = request.getHeader("X-Real-IP");

    if (realIp != null && !realIp.isEmpty()) {
        return realIp;
    }

    System.out.println("X-Forwarded-For: " + request.getHeader("X-Forwarded-For"));
System.out.println("X-Real-IP: " + request.getHeader("X-Real-IP"));
System.out.println("RemoteAddr: " + request.getRemoteAddr());

    return request.getRemoteAddr();
}


}