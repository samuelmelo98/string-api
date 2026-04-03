
package org.stringtecnologia.string_api.resources;

import org.stringtecnologia.string_api.repository.DocumentoRepository;

import org.stringtecnologia.string_api.model.dto.Documento; // ✅ correto


import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;

import java.util.Map;

import java.util.Map;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/api/validacao")
public class ValidacaoController {

    @Autowired
    private DocumentoRepository repository;

    @GetMapping("/{codigo}")
    public ResponseEntity<?> validar(@PathVariable String codigo) {

        Documento doc = repository.buscar(codigo);

        if (doc == null) {
            return ResponseEntity.status(404).body(Map.of(
                "valido", false,
                "mensagem", "Documento NÃO encontrado"
            ));
        }

        return ResponseEntity.ok(Map.of(
            "valido", true,
            "usuario", doc.getUsuario(),
            "data", doc.getData()
        ));
    }
}