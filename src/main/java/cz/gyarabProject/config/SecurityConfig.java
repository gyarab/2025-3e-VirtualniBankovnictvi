package cz.gyarabProject.config;

import cz.gyarabProject.database.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    private final UserService user;

    public SecurityConfig(UserService user) {
        this.user = user;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                    auth ->
                            auth.requestMatchers("/register", "/login", "/css/**")
                            .permitAll().anyRequest().authenticated())
                .formLogin(form ->
                    form.loginPage("/login").defaultSuccessUrl("/dashboard", true)
                            .permitAll())
                .logout(logout ->
                    logout.logoutUrl("/logout").logoutSuccessUrl("/login?logout"));
        return http.build();
    }
}
