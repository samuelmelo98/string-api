package org.stringtecnologia.string_api.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.stringtecnologia.string_api.model.interfaces.DocumentoProcessorGenerico;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import tools.jackson.databind.ObjectMapper;

import java.util.Locale;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ThymeleafProcessorGenerico
        implements DocumentoProcessorGenerico {

    private final TemplateEngine templateEngine;
    private final ObjectMapper objectMapper;

    @Override
    public <T> String process(
            String template,
            T data
    ) {

        Context context =
                new Context(
                        Locale.of("pt", "BR")
                );

        context.setVariables(
                objectMapper.convertValue(
                        data,
                        Map.class
                )
        );

        return templateEngine.process(
                template,
                context
        );
    }
}