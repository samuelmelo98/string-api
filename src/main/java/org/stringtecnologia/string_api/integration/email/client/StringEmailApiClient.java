package org.stringtecnologia.string_api.integration.email.client;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import org.stringtecnologia.string_api.integration.email.dto.EmailEnvioRequestDTO;
import org.stringtecnologia.string_api.integration.email.dto.EmailEnvioResponseDTO;

@HttpExchange(accept = MediaType.APPLICATION_JSON_VALUE)
public interface StringEmailApiClient {

    @PostExchange(value = "/api/emails", contentType = MediaType.APPLICATION_JSON_VALUE)
    EmailEnvioResponseDTO enviar(@RequestBody EmailEnvioRequestDTO request);
}
