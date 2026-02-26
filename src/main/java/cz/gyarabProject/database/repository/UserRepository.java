package cz.gyarabProject.database.repository;

import cz.gyarabProject.database.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsUserByUsername(String name);
    Optional<User> findByUsername(String name);
    User getUserByUsername(String name);
    User getUserById(Long id);
}
