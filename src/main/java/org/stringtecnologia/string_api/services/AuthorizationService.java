package org.stringtecnologia.string_api.services;


import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.stringtecnologia.string_api.resources.security.KeycloakRoleService;
import org.stringtecnologia.string_api.services.PermissaoService;

import java.util.Locale;
import java.util.Set;

@Component("authz")
@RequiredArgsConstructor
public class AuthorizationService {

    private final KeycloakRoleService keycloakRoleService;
    private final PermissaoService permissaoService;

    public boolean has(
            Authentication authentication,
            String permissao
    ) {

        if (authentication == null
                || !authentication.isAuthenticated()
                || permissao == null
                || permissao.isBlank()) {
            return false;
        }

        if (!(authentication instanceof JwtAuthenticationToken jwtAuthentication)) {
            return false;
        }

        Set<String> roles =
                keycloakRoleService.buscarRoles(
                        jwtAuthentication.getToken()
                );

        if (roles.isEmpty()) {
            return false;
        }

        Set<String> permissoes =
                permissaoService.buscarPorPerfis(roles);

        return permissoes.contains(
                permissao
                        .trim()
                        .toUpperCase(Locale.ROOT)
        );
    }
}
