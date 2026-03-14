package org.stringtecnologia.string_api.resources;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.stringtecnologia.string_api.model.dto.UserCreateDTO;
import org.stringtecnologia.string_api.model.dto.UserDTO;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping
public class TesteController {

    private final List<UserDTO> users = new CopyOnWriteArrayList<>();

    @GetMapping("/hello")
    public Map<String, Object> hello(Authentication authentication) {
        Jwt jwt = (Jwt) authentication.getPrincipal();

        Map<String, Object> realmAccess = jwt.getClaim("realm_access");
        @SuppressWarnings("unchecked")
        var roles = realmAccess != null
                ? (Iterable<String>) realmAccess.get("roles")
                : List.of();

        return Map.of(
                "message", "Backend acessado com sucesso!",
                "user", jwt.getClaimAsString("preferred_username"),
                "roles", roles
        );
    }

            // 🔹 POST PARA O ANGULAR
        @PostMapping("/users/teste")
        public Map<String, Object> createUser(
                @RequestBody UserCreateDTO dto,
                Authentication authentication
        ) {

            Jwt jwt = (Jwt) authentication.getPrincipal();

            // 🔹 Aqui depois você persiste no banco
            return Map.of(
                    "name", dto.getName(),
                    "email", dto.getEmail(),
                    "createdBy", jwt.getClaimAsString("preferred_username"),
                    "status", "CREATED"
            );
        }

    // 🔹 LISTAR USUÁRIOS
    @GetMapping("/users")
    public List<UserDTO> list(Authentication authentication) {
        return users;
    }

    // 🔹 CADASTRAR USUÁRIO
    @PostMapping("/users")
    public UserDTO create(
            @RequestBody UserDTO user,
            Authentication authentication
    ) {
        Jwt jwt = (Jwt) authentication.getPrincipal();

        System.out.println("Usuário autenticado: " +
                jwt.getClaimAsString("preferred_username"));

        users.add(user);
        return user;
    }
    }



