package org.stringtecnologia.string_api.integration.infosimples.client;



import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import org.stringtecnologia.string_api.integration.infosimples.dto.InfosimplesCpfPageResponse;

import java.util.Map;

@HttpExchange("/consultas")
public interface InfosimplesApiClient {

    @PostExchange(value = "/receita-federal/cpf", contentType = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    InfosimplesCpfPageResponse consultarCpf(@RequestParam MultiValueMap<String, String> params);
}
