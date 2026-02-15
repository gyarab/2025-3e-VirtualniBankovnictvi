package cz.gyarabProject.database.kb.service;

import cz.gyarabProject.database.kb.entity.User;
import cz.gyarabProject.database.kb.repository.UserRepository;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository user;
    private final Argon2PasswordEncoder encoder;

    public UserService(UserRepository user) {
        this.user = user;
        encoder = Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();
    }

    public User register(String username, String password) {
        if (username == null || password == null) {
            throw new IllegalArgumentException("Username or password cannot be null.");
        } else if (user.existsUserByUsername(username)) {
            throw new IllegalStateException("User " + username + " already exists.");
        }
        return user.save(new User(username, hashPassword(password)));
    }

    public boolean login(String username, String password) {
        if (username == null || password == null) {
            throw new IllegalArgumentException("Username or password cannot be null.");
        } else if (!user.existsUserByUsername(username)) {
            throw new IllegalStateException("User " + username + " does not exist.");
        }
        User user = this.user.findByUsername(username);
        return comparePasswords(password, user.getPassword());
    }

    public String hashPassword(String password) {
        return encoder.encode(password);
    }

    public boolean comparePasswords(String rawPassword, String encodedPassword) {
        return encoder.matches(rawPassword, encodedPassword);
    }
}
