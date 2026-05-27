package org.stringtecnologia.string_api.repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.stringtecnologia.string_api.model.dto.Documento;
import org.stringtecnologia.string_api.model.entities.Cliente;
import org.stringtecnologia.string_api.model.entities.User;

@Repository

public interface UserRepository extends JpaRepository<User, Long> {


}