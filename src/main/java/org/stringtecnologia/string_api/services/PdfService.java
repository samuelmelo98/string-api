package org.stringtecnologia.string_api.services;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.stringtecnologia.string_api.model.entities.DocumentTemplate;
import org.stringtecnologia.string_api.repository.DocumentRepository;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.ByteArrayOutputStream;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PdfService {


    private final TemplateEngine templateEngine;
    private final DocumentRepository documentRepository;

    public byte[] gerarPdf(Map<String, Object> dados) throws Exception {

        System.out.println(
                getClass()
                        .getClassLoader()
                        .getResource("templates/pdf/pdf.html")
        );

        DocumentTemplate template =
                documentRepository
                        .findFirstBySlugAndActiveTrueOrderByVersionDesc("termo-adiantamento")
                        .orElseThrow();


        
        
        Context context = new Context();
        context.setVariables(dados);


        String html = templateEngine.process(
                template.getTemplate(),
                context
        );

        // Renderiza template Thymeleaf
      //  String html = templateEngine.process("pdf/pdf", context);


        // Remove BOM UTF-8 invisível
    //    html = html.replace("\uFEFF", "");

        // Converte HTML -> XHTML válido
        Document document = Jsoup.parse(html);

        document.outputSettings()
                .syntax(Document.OutputSettings.Syntax.xml)
                .escapeMode(org.jsoup.nodes.Entities.EscapeMode.xhtml)
                .prettyPrint(true);

        String xhtml = document.html();

        ByteArrayOutputStream output = new ByteArrayOutputStream();

        PdfRendererBuilder builder = new PdfRendererBuilder();

        builder.useFastMode();

        builder.withHtmlContent(xhtml, null);

        builder.toStream(output);

        builder.run();

        return output.toByteArray();
    }
}