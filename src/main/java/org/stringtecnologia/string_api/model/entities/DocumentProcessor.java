package org.stringtecnologia.string_api.model.entities;

import java.util.Map;

public interface DocumentProcessor {
    String process(String template, Map<String, Object> variables);
}