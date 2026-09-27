package org.stringtecnologia.string_api.resources.security;



import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.stringtecnologia.string_api.model.dto.security.UsuarioAutenticadoDTO;
import org.stringtecnologia.string_api.services.PermissaoService;

import java.util.Set;
import java.util.TreeSet;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SecurityController {

    private final KeycloakRoleService keycloakRoleService;
    private final PermissaoService permissaoService;

    @GetMapping("/me")
    public ResponseEntity<UsuarioAutenticadoDTO> me(
            JwtAuthenticationToken authentication
    ) {

        var jwt = authentication.getToken();

        Set<String> roles =
                keycloakRoleService.buscarRoles(jwt);

        Set<String> permissoes =
                permissaoService.buscarPorPerfis(roles);

        UsuarioAutenticadoDTO response =
                new UsuarioAutenticadoDTO(
                        jwt.getSubject(),
                        jwt.getClaimAsString("preferred_username"),
                        jwt.getClaimAsString("name"),
                        jwt.getClaimAsString("email"),
                        new TreeSet<>(roles),
                        new TreeSet<>(permissoes)
                );

        return ResponseEntity.ok(response);
    }
}
