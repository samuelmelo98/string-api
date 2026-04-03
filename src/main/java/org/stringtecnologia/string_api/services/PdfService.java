package org.stringtecnologia.string_api.services;





import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;
import java.io.ByteArrayOutputStream;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;



@Service
public class PdfService {

    @Autowired
    private TemplateEngine templateEngine;

    public byte[] gerarPdf(Map<String, Object> dados) throws Exception {

        Context context = new Context();
        context.setVariables(dados);

        // Processa HTML com Thymeleaf
       String html = templateEngine.process("pdf/pdf", context);

        ByteArrayOutputStream output = new ByteArrayOutputStream();

        PdfRendererBuilder builder = new PdfRendererBuilder();
        builder.withHtmlContent(html, null);
        builder.toStream(output);
        builder.run();

        return output.toByteArray();
    }
}