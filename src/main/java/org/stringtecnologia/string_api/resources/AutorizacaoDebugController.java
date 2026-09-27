package org.stringtecnologia.string_api.resources;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.stringtecnologia.string_api.model.dto.security.AutorizacaoDebugDTO;
import org.stringtecnologia.string_api.resources.security.KeycloakRoleService;
import org.stringtecnologia.string_api.services.PermissaoService;

import java.util.Set;
import java.util.TreeSet;

@RestController
@RequestMapping("/api/debug/autorizacao")
@RequiredArgsConstructor
public class AutorizacaoDebugController {

    private final KeycloakRoleService keycloakRoleService;

    private final PermissaoService permissaoService;

    @GetMapping("/me")
    public ResponseEntity<AutorizacaoDebugDTO> me(
            JwtAuthenticationToken authentication
    ) {

        var jwt = authentication.getToken();

        Set<String> roles =
                keycloakRoleService.buscarRoles(jwt);

        Set<String> permissoes =
                permissaoService.buscarPorPerfis(roles);

        AutorizacaoDebugDTO response =
                new AutorizacaoDebugDTO(
                        jwt.getSubject(),
                        jwt.getClaimAsString(
                                "preferred_username"
                        ),
                        keycloakRoleService.getClientId(),
                        new TreeSet<>(roles),
                        new TreeSet<>(permissoes)
                );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/permitido")
    @PreAuthorize(
            "@authz.has(authentication, 'CLIENTE_VISUALIZAR')"
    )
    public ResponseEntity<String> permitido() {
        return ResponseEntity.ok("ACESSO PERMITIDO");
    }

    @GetMapping("/negado")
    @PreAuthorize(
            "@authz.has(authentication, 'PERMISSAO_INEXISTENTE')"
    )
    public ResponseEntity<String> negado() {
        return ResponseEntity.ok("NÃO DEVERIA CHEGAR AQUI");
    }
}