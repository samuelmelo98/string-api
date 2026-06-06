package org.stringtecnologia.string_api.model.interfaces;

public interface DocumentoProcessorGenerico {

        <T> String process(
                String template,
                T data
        );
    }
