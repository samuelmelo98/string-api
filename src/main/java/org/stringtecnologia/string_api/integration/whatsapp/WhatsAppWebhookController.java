package org.stringtecnologia.string_api.integration.whatsapp;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public/webhooks/whatsapp")
public class WhatsAppWebhookController {

    @Value("${whatsapp.webhook.verify-token}")
    private String verifyToken;

    @GetMapping
    public ResponseEntity<String> verificarWebhook(
            @RequestParam(name = "hub.mode") String mode,
            @RequestParam(name = "hub.verify_token") String token,
            @RequestParam(name = "hub.challenge") String challenge) {

        if ("subscribe".equals(mode) && verifyToken.equals(token)) {
            return ResponseEntity.ok(challenge);
        }

        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @PostMapping
    public ResponseEntity<Void> receberEvento(
            @RequestBody String payload) {

        System.out.println(payload);

        return ResponseEntity.ok().build();
    }
}