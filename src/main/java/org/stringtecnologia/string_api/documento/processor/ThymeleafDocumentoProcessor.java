package org.stringtecnologia.string_api.documento.processor;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.stereotype.Component;
import org.stringtecnologia.string_api.model.interfaces.DocumentoProcessor;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.beans.PropertyDescriptor;
import java.util.Locale;


@Component
@RequiredArgsConstructor
public class ThymeleafDocumentoProcessor implements DocumentoProcessor {

    private final TemplateEngine templateEngine;


    @Override
    public String process(
            String template,
            Object data
    ) {

        Context context = new Context(
                Locale.forLanguageTag("pt-BR")
        );

        BeanWrapper wrapper =
                new BeanWrapperImpl(data);

        for (PropertyDescriptor pd :
                wrapper.getPropertyDescriptors()) {

            String nome = pd.getName();

            if (!"class".equals(nome)) {

                context.setVariable(
                        nome,
                        wrapper.getPropertyValue(nome)
                );
            }
        }

        return templateEngine.process(
                template,
                context
        );
    }
}