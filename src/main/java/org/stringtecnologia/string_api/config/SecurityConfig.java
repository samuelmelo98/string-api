package org.stringtecnologia.string_api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http

                /*
                 * CORS
                 */
                .cors(cors ->
                        cors.configurationSource(
                                corsConfigurationSource()
                        )
                )

                /*
                 * API REST usando JWT.
                 * Não utilizamos sessão/cookie para autenticação.
                 */
                .csrf(csrf ->
                        csrf.disable()
                )

                /*
                 * API Stateless.
                 * Cada requisição deve apresentar seu JWT.
                 */
                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                /*
                 * Regras HTTP.
                 *
                 * A autorização granular será feita posteriormente
                 * nos services através de:
                 *
                 * @PreAuthorize(
                 *     "@authz.has(authentication, 'CLIENTE_EDITAR')"
                 * )
                 */
                .authorizeHttpRequests(auth -> auth

                        /*
                         * Libera preflight CORS.
                         */
                        .requestMatchers(
                                HttpMethod.OPTIONS,
                                "/**"
                        ).permitAll()

                        /*
                         * Endpoint interno de erro.
                         */
                        .requestMatchers(
                                "/error"
                        ).permitAll()

                        /*
                         * Consulta pública de Ordem de Serviço.
                         */
                        .requestMatchers(
                                "/api/public/ordens-servico/**"
                        ).permitAll()

                        /*
                         * Endpoints atualmente públicos.
                         *
                         * Vamos revisar estes individualmente
                         * durante a migração para permissões.
                         */
                        .requestMatchers(
                                "/api/validacao/**"
                        ).permitAll()

                        .requestMatchers(
                                "/api/v1/documents/**"
                        ).permitAll()

                        .requestMatchers(
                                "/api/v1/cpf/**"
                        ).permitAll()

                        .requestMatchers(
                                "/graphql/**"
                        ).permitAll()

                        .requestMatchers(
                                "/documentos/v1/**"
                        ).permitAll()

                        .requestMatchers(
                                "/api/public/webhooks/whatsapp/**"
                        ).permitAll()

                        /*
                         * Todo o restante exige JWT válido.
                         */
                        .anyRequest()
                        .authenticated()
                )

                /*
                 * Spring Resource Server.
                 *
                 * Não precisamos mais de
                 * KeycloakRealmRoleConverter.
                 *
                 * As roles da aplicação serão obtidas por:
                 *
                 * resource_access
                 *   -> string-api
                 *      -> roles
                 *
                 * através do KeycloakRoleService.
                 */
                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(
                                Customizer.withDefaults()
                        )
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration =
                new CorsConfiguration();

        /*
         * Frontends autorizados a chamar a API.
         */
        configuration.setAllowedOrigins(
                List.of(
                        "https://app.cluster.stringtecnologiadf.org",
                        "https://site-html.cluster.stringtecnologiadf.org",
                        "http://localhost:4200",
                        "https://stringtecnologiadf.org:4200"
                )
        );

        configuration.setAllowedMethods(
                List.of(
                        "GET",
                        "POST",
                        "PUT",
                        "PATCH",
                        "DELETE",
                        "OPTIONS"
                )
        );

        configuration.setAllowedHeaders(
                List.of(
                        "Authorization",
                        "Content-Type",
                        "Cache-Control",
                        "X-Requested-With",
                        "Accept",
                        "Origin"
                )
        );

        /*
         * Permite envio de Authorization e,
         * caso exista, cookies entre origens autorizadas.
         */
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
                "/**",
                configuration
        );

        return source;
    }
}