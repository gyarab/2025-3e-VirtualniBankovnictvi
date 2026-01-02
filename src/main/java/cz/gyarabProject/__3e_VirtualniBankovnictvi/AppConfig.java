package cz.gyarabProject.__3e_VirtualniBankovnictvi;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.http.HttpClient;
import java.time.Duration;

/**
 * Configuration of Spring Boot app.
 */
@Configuration
public class AppConfig {
    /**
     * Configuration of Spring Boot dependency injection to set {@link HttpClient} as sengleton.
     */
    @Bean
    public HttpClient httpClient() {
        return HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(20)).build();
    }
}
