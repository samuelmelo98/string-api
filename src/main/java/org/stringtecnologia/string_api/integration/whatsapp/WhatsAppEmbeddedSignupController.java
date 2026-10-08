package org.stringtecnologia.string_api.integration.whatsapp;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public/whatsapp/embedded-signup")
public class WhatsAppEmbeddedSignupController {

    @GetMapping("/callback")
    public ResponseEntity<String> callback(
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String error,
            @RequestParam(name = "error_description", required = false)
            String errorDescription) {

        if (error != null) {
            return ResponseEntity
                    .badRequest()
                    .body("Falha no cadastro do WhatsApp: " + error);
        }

        if (code == null || code.isBlank()) {
            return ResponseEntity
                    .badRequest()
                    .body("Código de autorização não informado.");
        }

        // Próximo passo:
        // 1. validar state
        // 2. trocar code por access token
        // 3. recuperar WABA ID
        // 4. recuperar Phone Number ID
        // 5. persistir a integração

        return ResponseEntity.ok(
                "Cadastro do WhatsApp recebido com sucesso."
        );
    }
}