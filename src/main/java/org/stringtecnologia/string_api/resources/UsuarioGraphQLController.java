package org.stringtecnologia.string_api.resources;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.stringtecnologia.string_api.model.dto.BuscarUserInput;
import org.stringtecnologia.string_api.model.entities.User;
import org.stringtecnologia.string_api.repository.UserRepository;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class UsuarioGraphQLController {
    private final UserRepository userRepository;

    @QueryMapping
    public List<User> users() {
        return userRepository.findAll();
    }

    @QueryMapping
    public User buscarUser(@Argument BuscarUserInput input) {
         return null;
    }

    @QueryMapping
    public User user(@Argument Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @MutationMapping
    public User criarUser(
            @Argument String nome,
            @Argument String email) {

        User usuario = new User();
        usuario.setNome(nome);
        usuario.setEmail(email);

        return userRepository.save(usuario);
    }
}
