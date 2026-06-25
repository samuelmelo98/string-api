package org.stringtecnologia.string_api.documento.processor;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.stringtecnologia.string_api.model.interfaces.DocumentoProcessor;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import java.util.Locale;
import java.util.Map;


@Component
@RequiredArgsConstructor
public class ThymeleafDocumentoProcessor implements DocumentoProcessor {

    private final TemplateEngine templateEngine;


    @Override
    public String process(
            String template,
            Map<String,Object> dados
    ) {

        Context context =
                new Context(
                        Locale.forLanguageTag("pt-BR")
                );

        dados.forEach(
                context::setVariable
        );

        return templateEngine.process(
                template,
                context
        );
    }
}