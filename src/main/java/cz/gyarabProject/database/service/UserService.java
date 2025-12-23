package cz.gyarabProject.database.service;

import cz.gyarabProject.database.entity.User;
import cz.gyarabProject.database.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository user;

    public UserService(UserRepository user) {
        this.user = user;
    }

    public User register(String username, String password) {
        if (username == null || password == null) {
            throw new IllegalArgumentException("Username or password cannot be null.");
        } else if (user.existsUserByUsername(username)) {
            throw new IllegalStateException("User " + username + " already exists.");
        }
        return user.save(new User(username, password));
    }
}
