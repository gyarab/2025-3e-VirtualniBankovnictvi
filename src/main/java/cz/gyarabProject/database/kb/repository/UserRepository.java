package cz.gyarabProject.database.kb.repository;

import cz.gyarabProject.database.kb.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsUserByUsername(String username);
    User findByUsername(String username);
}
