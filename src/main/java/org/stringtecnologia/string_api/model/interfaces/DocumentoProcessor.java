package org.stringtecnologia.string_api.model.interfaces;

public interface DocumentoProcessor {
    //String process(String template, Map<String, Object> variables);

    String process(
            String template,
            Object data
    );
}
