package org.stringtecnologia.string_api.resources;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.stringtecnologia.string_api.model.dto.UserCreateDTO;
import org.stringtecnologia.string_api.model.dto.UserDTO;
import org.stringtecnologia.string_api.model.dto.avatar.AvatarDTO;
import org.stringtecnologia.string_api.services.UserService;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final List<UserDTO> users = new CopyOnWriteArrayList<>();

    private final UserService userService;

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
        return userService.all();
       // return users;
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
        userService.salvarUsuario(user);
        users.add(user);
        return user;
    }

    @PostMapping("/usuarios/avatar")
    public AvatarDTO upload(
            @RequestParam("arquivo") MultipartFile arquivo
    ) throws Exception {

        String nome = userService.uploudAvatar(arquivo);

        return new AvatarDTO(
                nome,
                "/usuarios/avatar/" + nome
        );
    }

    @GetMapping("/usuarios/avatar/{arquivo}")
    public ResponseEntity<Resource> avatar(
            @PathVariable String arquivo) {

        return userService.recuperarAvatar(arquivo);
    }

    }



