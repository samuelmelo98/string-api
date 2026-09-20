package org.stringtecnologia.string_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.stringtecnologia.string_api.model.entities.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository
        extends JpaRepository<User, Long> {

    Optional<User> findByEmail(
            String email
    );

    List<User> findByAtivoTrueOrderByNomeAsc();
}