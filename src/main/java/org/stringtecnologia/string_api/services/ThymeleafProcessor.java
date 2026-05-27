package org.stringtecnologia.string_api.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.stringtecnologia.string_api.model.entities.DocumentProcessor;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class ThymeleafProcessor implements DocumentProcessor {

    private final TemplateEngine templateEngine;

    @Override
    public String process(String templateContent, Map<String, Object> data) {

        Context context = new Context();
        context.setVariables(data);

        return templateEngine.process(templateContent, context);
    }
}