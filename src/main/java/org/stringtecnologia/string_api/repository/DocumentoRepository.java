package org.stringtecnologia.string_api.repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;
import org.stringtecnologia.string_api.model.dto.Documento;

@Component
public class DocumentoRepository {

    private final Map<String, Documento> banco = new ConcurrentHashMap<>();

    public void salvar(Documento doc) {
        banco.put(doc.getCodigo(), doc);
    }

    public Documento buscar(String codigo) {
        return banco.get(codigo);
    }
}