package org.stringtecnologia.string_api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. Ativa a configuração de CORS definida no Bean abaixo
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // 2. Desativa CSRF (comum para APIs Stateless/JWT)
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // 3. Libera o "check" do navegador (Preflight) para todas as rotas
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // 4. Libera o endpoint de erro para que o log mostre a causa real de falhas
                        .requestMatchers("/error").permitAll()
                        // ✅ LIBERA PDF
           // .requestMatchers("/api/relatorios/**").permitAll()
           // .requestMatchers("/api/validacao/**").permitAll()

                        // 5. Suas regras de permissão por Role
                        .requestMatchers("/api/hello").hasRole("frontend-user")

                        // 6. Qualquer outra requisição exige autenticação
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(jwt ->
                                jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())
                        )
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Origens permitidas (Subdomínios diferentes são tratados como origens diferentes)
        configuration.setAllowedOrigins(List.of(
                "https://app.cluster.stringtecnologiadf.org",
                "https://site.cluster.stringtecnologiadf.org",
                "http://localhost:4200",
		"https://stringtecnologiadf.org:4200"
        ));

        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Headers permitidos para o Angular e Keycloak
        configuration.setAllowedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "Cache-Control",
                "X-Requested-With",
                "Accept",
                "Origin"
        ));

        // Necessário para cookies e cabeçalhos de autenticação entre domínios
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        // Usa o seu conversor customizado que extrai as roles do 'realm_access' do JWT
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new KeycloakRealmRoleConverter());
        return jwtAuthenticationConverter;
    }
}