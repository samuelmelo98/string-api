package org.stringtecnologia.string_api.resources;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

import org.stringtecnologia.string_api.model.dto.teste.ClienteDTO;
import org.stringtecnologia.string_api.model.dto.teste.ItemDTO;
import org.stringtecnologia.string_api.model.dto.teste.RelatorioTesteDTO;
import org.stringtecnologia.string_api.services.GeradorDocumentoGenericoService;
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
import org.stringtecnologia.string_api.util.TipoDocumentoDominio;

import javax.sql.DataSource;


@RestController
@RequestMapping("/api/relatorios")
@RequiredArgsConstructor
public class RelatorioController {

    @Value("${app.frontend-url}")
private String frontendUrl;

    @Autowired
    private PdfService pdfService;

    @Autowired
private DocumentoRepository repository;

    private final GeradorDocumentoGenericoService geradorDocumentoGenericoService;

  @GetMapping("/pdf")
public ResponseEntity<byte[]>  gerarPdf(HttpServletRequest request) throws Exception {

    String codigo = UUID.randomUUID().toString();

    String baseUrl = request.getScheme() + "://" +
                     request.getServerName() + ":" +
                     request.getServerPort();


                     String urlValidacao = frontendUrl + "/validacao/" + codigo;

   // String urlValidacao = baseUrl + "/api/validacao/" + codigo;

    System.out.println("CODIGO: " + codigo);

    Map<String, Object> dados = new HashMap<>();

    String ip = getClientIp(request);
    String ip2= getClientIp2(request);


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

//    repository.salvar(doc);

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

    public String getClientIp2(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");

        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        // Caso venha múltiplos IPs: client, proxy1, proxy2
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }

        return ip;
    }




//    @GetMapping("/pdf/teste")
//    public ResponseEntity<byte[]>  gerarPdfTeste(HttpServletRequest request) throws Exception {
//     geradorDocumentoGenericoService.gerarDocumento("teste", this.geraModeloDoc(),1L, TipoDocumentoDominio.TESTE);
//     return null;
//    }

    @GetMapping("/pdf/teste")
    public ResponseEntity<byte[]> gerarPdfTeste() {

        byte[] pdf =
                geradorDocumentoGenericoService
                        .gerarPdfSemSalvar(
                                "teste",
                                geraModeloDoc()
                        );

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=teste.pdf"
                )
                .body(pdf);
    }


private RelatorioTesteDTO geraModeloDoc(){
    RelatorioTesteDTO dto =
            new RelatorioTesteDTO(

                    "Relatório Completo Thymeleaf",

                    "Samuel Silva",

                    LocalDate.now(),

                    BigDecimal.valueOf(1500.75),

                    true,

                    "https://site-html.cluster.stringtecnologiadf.org",

                    new ClienteDTO(
                            "Empresa XPTO",
                            "contato@empresa.com",
                            "(61) 99999-9999"
                    ),

                    List.of(

                            new ItemDTO(
                                    1L,
                                    "Notebook Dell",
                                    2,
                                    BigDecimal.valueOf(3500)
                            ),

                            new ItemDTO(
                                    2L,
                                    "Monitor LG",
                                    1,
                                    BigDecimal.valueOf(1200)
                            )

                    )
            );
    return dto;
}
}