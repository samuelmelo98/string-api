package org.stringtecnologia.string_api.integration.email.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import org.stringtecnologia.string_api.integration.email.client.StringEmailApiClient;
import org.stringtecnologia.string_api.integration.email.security.StringEmailTokenProvider;

import java.net.http.HttpClient;
import java.time.Duration;

@Configuration
public class StringEmailClientConfig {

    @Bean
    public RestClient stringEmailTokenRestClient(StringEmailApiProperties properties) {
        return RestClient.builder()
                .requestFactory(requestFactory(properties))
                .baseUrl(properties.tokenUrl())
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Bean
    public RestClient stringEmailRestClient(
            StringEmailApiProperties properties,
            StringEmailTokenProvider tokenProvider
    ) {
        return RestClient.builder()
                .requestFactory(requestFactory(properties))
                .baseUrl(properties.apiBaseUrl())
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .requestInterceptor((request, body, execution) -> {
                    request.getHeaders().setBearerAuth(tokenProvider.getAccessToken());
                    return execution.execute(request, body);
                })
                .build();
    }

    @Bean
    public StringEmailApiClient stringEmailApiClient(RestClient stringEmailRestClient) {
        HttpServiceProxyFactory factory = HttpServiceProxyFactory
                .builderFor(RestClientAdapter.create(stringEmailRestClient))
                .build();

        return factory.createClient(StringEmailApiClient.class);
    }

    private JdkClientHttpRequestFactory requestFactory(StringEmailApiProperties properties) {
        Duration connectTimeout = properties.connectTimeout() != null
                ? properties.connectTimeout()
                : Duration.ofSeconds(5);

        Duration readTimeout = properties.readTimeout() != null
                ? properties.readTimeout()
                : Duration.ofSeconds(15);

        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(connectTimeout)
                .build();

        JdkClientHttpRequestFactory factory = new JdkClientHttpRequestFactory(httpClient);
        factory.setReadTimeout(readTimeout);
        return factory;
    }
}
