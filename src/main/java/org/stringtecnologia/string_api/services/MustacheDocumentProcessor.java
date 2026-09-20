package org.stringtecnologia.string_api.services;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import org.springframework.stereotype.Component;
import org.stringtecnologia.string_api.model.entities.DocumentProcessor;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;

@Component("mustacheDocumentProcessor")
public class MustacheDocumentProcessor
        implements DocumentProcessor {

    private final MustacheFactory mustacheFactory =
            new DefaultMustacheFactory();

    @Override
    public String process(
            String template,
            Map<String, Object> variables
    ) {

        Mustache mustache =
                mustacheFactory.compile(
                        new StringReader(template),
                        "documento"
                );

        StringWriter writer =
                new StringWriter();

        mustache.execute(
                writer,
                variables
        );

        return writer.toString();
    }
}