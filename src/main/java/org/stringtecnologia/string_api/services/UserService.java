package org.stringtecnologia.string_api.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.stringtecnologia.string_api.model.dto.UserCreateDTO;
import org.stringtecnologia.string_api.model.dto.UserDTO;
import org.stringtecnologia.string_api.model.entities.User;
import org.stringtecnologia.string_api.repository.UserRepository;

import java.util.Collections;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User salvarUsuario(UserDTO user) {
        User user1 = new User();
        user1.setNome(user.name());
        user1.setEmail(user.email());


        try {
            return userRepository.save(user1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<UserDTO> all() {
        try {

            List<User> users = userRepository.findAll();

            return users.stream()
                    .map(user -> new UserDTO(
                            user.getNome(),
                            user.getEmail()
                    ))
                    .toList();

        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }


}
