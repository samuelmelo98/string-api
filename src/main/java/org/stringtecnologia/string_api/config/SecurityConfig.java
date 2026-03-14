package org.stringtecnologia.string_api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        // 1. O CORS deve ser configurado primeiro
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(auth -> auth
            // 2. LIBERAÇÃO EXPLÍCITA PARA O NAVEGADOR (Preflight)
            .requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()
            
            // Suas outras rotas
            .requestMatchers("/api/hello").hasRole("frontend-user")
            .anyRequest().authenticated()
        )
        .oauth2ResourceServer(oauth2 ->
            oauth2.jwt(jwt ->
                jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())
            )
        );

    return http.build();
}

    // 2. CONFIGURAÇÃO DE CORS (O segredo para o Angular funcionar)
    @Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    
    // 👇 ADICIONE AMBOS OU O QUE VOCÊ REALMENTE USA NO NAVEGADOR
    configuration.setAllowedOrigins(List.of(
        "https://app.cluster.stringtecnologiadf.org", 
        "https://site.cluster.stringtecnologiadf.org"
    ));
    
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    
    // 👇 ADICIONE ESTE HEADER (O Angular costuma enviar para o Keycloak)
    configuration.setAllowedHeaders(Arrays.asList(
        "Authorization", 
        "Content-Type", 
        "Cache-Control", 
        "X-Requested-With"
    ));
    
    configuration.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
}

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        // Use apenas o seu conversor customizado (KeycloakRealmRoleConverter)
        // Certifique-se que dentro dele você está buscando o claim "realm_access"
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new KeycloakRealmRoleConverter());
        return jwtAuthenticationConverter;
    }
}