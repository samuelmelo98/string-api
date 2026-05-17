package org.stringtecnologia.string_api.integration.infosimples.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import org.stringtecnologia.string_api.integration.infosimples.client.InfosimplesApiClient;

import java.net.http.HttpClient;

@Configuration
public class InfosimplesClientConfig {

    @Bean
    public RestClient infosimplesRestClient(InfosimplesApiProperties props) {

        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(props.timeOut())
                .build();

        JdkClientHttpRequestFactory factory = new JdkClientHttpRequestFactory(httpClient);

        return RestClient.builder()
                .requestFactory(factory)
                .baseUrl(props.apiBaseUrl())
                .defaultHeader("Accept", "application/json")
                .build();
    }

    @Bean
    public InfosimplesApiClient infosimplesApiClient(RestClient infosimplesRestClient) {
        HttpServiceProxyFactory factory = HttpServiceProxyFactory
                .builderFor(RestClientAdapter.create(infosimplesRestClient))
                .build();

        return factory.createClient(InfosimplesApiClient.class);
    }
}
