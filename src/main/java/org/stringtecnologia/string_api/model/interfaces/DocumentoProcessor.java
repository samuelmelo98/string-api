package org.stringtecnologia.string_api.model.interfaces;

import java.util.Map;

public interface DocumentoProcessor {
    //String process(String template, Map<String, Object> variables);

    String process(
            String template,
            Map<String,Object> dados
    );
}
