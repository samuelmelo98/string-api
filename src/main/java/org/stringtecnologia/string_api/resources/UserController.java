package org.stringtecnologia.string_api.resources;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.stringtecnologia.string_api.model.dto.UserCreateDTO;
import org.stringtecnologia.string_api.model.dto.UserDTO;
import org.stringtecnologia.string_api.model.dto.avatar.AvatarDTO;
import org.stringtecnologia.string_api.model.dto.avatar.UserProfileDTO;
import org.stringtecnologia.string_api.model.dto.cliente.ClienteResponseDTO;
import org.stringtecnologia.string_api.model.entities.User;
import org.stringtecnologia.string_api.repository.UserRepository;
import org.stringtecnologia.string_api.services.ClienteService;
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
    private final UserRepository userRepository;
    private final ClienteService clienteService;

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
            @RequestParam("arquivo") MultipartFile arquivo,
            Authentication authentication
    ) throws Exception {

        Jwt jwt = (Jwt) authentication.getPrincipal();

        String email = jwt.getClaimAsString("email");

        return userService.uploadAvatar(arquivo, email);
    }

    @GetMapping("/usuarios/{id}/avatar")
    public ResponseEntity<Resource> avatar(@PathVariable Long id) {
        return userService.recuperarAvatar(id);
    }

    @GetMapping("/usuarios/me")
    public ResponseEntity<UserProfileDTO> me(Authentication authentication) {

        Jwt jwt = (Jwt) authentication.getPrincipal();

        String email = jwt.getClaimAsString("email");

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        String avatar = "/api/usuarios/" + user.getId() + "/avatar";


        return ResponseEntity.ok(
                new UserProfileDTO(
                        user.getId(),
                        user.getNome(),
                        user.getEmail(),
                        user.getNome(),
                        avatar
                )
        );
    }
    @GetMapping("/clientes")
    public ResponseEntity<Page<ClienteResponseDTO>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,asc") String sort,
            @RequestParam(required = false) String search) {

        String[] sortParams = sort.split(",");

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(
                        Sort.Direction.fromString(sortParams[1]),
                        sortParams[0]
                )
        );

        Page<ClienteResponseDTO> resultado =
                clienteService.listar(pageable, search);

        return ResponseEntity.ok(resultado);
    }


    }



