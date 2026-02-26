package cz.gyarabProject.database.entity;

import cz.gyarabProject.api.cs.datatype.Token;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, name = "name")
    private String username;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private String role;

    @Transient
    private final Token tokenCS = new Token();

    protected User() {}

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
