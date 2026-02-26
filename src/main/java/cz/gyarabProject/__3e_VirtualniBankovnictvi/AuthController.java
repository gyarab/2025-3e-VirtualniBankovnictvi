package cz.gyarabProject.__3e_VirtualniBankovnictvi;

import cz.gyarabProject.database.entity.User;
import cz.gyarabProject.database.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class AuthController {
    private final UserRepository user;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserRepository user, PasswordEncoder passwordEncoder) {
        this.user = user;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/register")
    public String registerForm() {
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String name, @RequestParam String password) {
        if (user.findByUsername(name).isPresent()) {
            return "redirect:/register?error";
        }
        user.save(User.builder()
                .username(name)
                .password(passwordEncoder.encode(password))
                .role("ROLE_USER")
                .build()
        );
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }

    @PostMapping("/login")
    public String handleLogin(@RequestParam String name,
                              @RequestParam String password,
                              HttpSession session) {
        Optional<User> userOpt = user.findByUsername(name);

        if (userOpt.isEmpty() || !passwordEncoder.matches(password, userOpt.get().getPassword())) {
            return "redirect:/login?error";
        }

        User user = userOpt.get();
        session.setAttribute("userId", user.getId());
        return "redirect:/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal org.springframework.security.core.userdetails.User user) {
        String name = user.getUsername();
        Optional<User> userOpt = this.user.findByUsername(name);
        if (userOpt.isEmpty()) {
            return "register";
        }
        Long userId = userOpt.get().getId();
        return "dashboard";
    }
}
