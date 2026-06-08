package org.stringtecnologia.string_api.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.stringtecnologia.string_api.config.storage.StorageProperties;
import org.stringtecnologia.string_api.model.dto.UserCreateDTO;
import org.stringtecnologia.string_api.model.dto.UserDTO;
import org.stringtecnologia.string_api.model.entities.User;
import org.stringtecnologia.string_api.repository.UserRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final StorageProperties storageProperties;

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

    public String uploudAvatar(MultipartFile arquivo)
            throws IOException {

        String extensao =
                arquivo.getOriginalFilename()
                        .substring(
                                arquivo.getOriginalFilename()
                                        .lastIndexOf("."));

        String nomeArquivo =
                UUID.randomUUID() + extensao;

        Path pasta = Paths.get(
                storageProperties.getPath(),
                "avatar"
        );

        Files.createDirectories(pasta);

        Path destino = pasta.resolve(nomeArquivo);

        Files.copy(
                arquivo.getInputStream(),
                destino,
                StandardCopyOption.REPLACE_EXISTING
        );

        return nomeArquivo;
    }

    public ResponseEntity recuperarAvatar(String arquivo) {

        Path path = Paths.get("documentos/avatar", arquivo);

        Resource resource = new FileSystemResource(path);

        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(resource);
    }


}
