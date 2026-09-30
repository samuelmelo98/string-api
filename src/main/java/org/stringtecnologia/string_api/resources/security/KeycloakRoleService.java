package org.stringtecnologia.string_api.resources.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@Service
public class KeycloakRoleService {

    private final String clientId;

    public KeycloakRoleService(
            @Value("${app.security.keycloak.client-id}")
            String clientId
    ) {
        this.clientId = clientId;
    }

    public Set<String> buscarRoles(Jwt jwt) {

        if (jwt == null) {
            return Set.of();
        }

        Map<String, Object> resourceAccess =
                jwt.getClaimAsMap("resource_access");

        if (resourceAccess == null || resourceAccess.isEmpty()) {
            return Set.of();
        }

        Object clientAccessObject =
                resourceAccess.get(clientId);

        if (!(clientAccessObject instanceof Map<?, ?> clientAccess)) {
            return Set.of();
        }

        Object rolesObject =
                clientAccess.get("roles");

        if (!(rolesObject instanceof Collection<?> roles)) {
            return Set.of();
        }

        Set<String> resultado = new LinkedHashSet<>();

        for (Object role : roles) {

            if (!(role instanceof String roleString)) {
                continue;
            }

            if (roleString.isBlank()) {
                continue;
            }

            resultado.add(
                    roleString
                            .trim()
                            .toUpperCase(Locale.ROOT)
            );
        }

        return Set.copyOf(resultado);
    }

    public String getClientId() {
        return clientId;
    }
}
