package cz.gyarabProject.__3e_VirtualniBankovnictvi;

import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import cz.gyarabProject.api.Requests;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

@RestController
public class TestOfApi {
    private final String propertiesPath = "./src/main/resources/";

    Properties props = new Properties();
    Requests requests;

    public TestOfApi() throws IOException {
        props.load(new FileInputStream(propertiesPath + "api.properties"));
        props.load(new FileInputStream(propertiesPath + "custom.properties"));
        requests = new Requests(props);
    }

    @GetMapping(value="/test-of-api/newJWT")
    public String newJWT() throws IOException, InterruptedException {
        System.out.println(System.getProperty("user.dir") + " test");
        try {
            requests.createNewTlsAuthentication();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return Files.readString(Paths.get("./keys-tokens/" + props.getProperty("jwt.path")));
    }

    @GetMapping(value="/test-of-api/registration")
    public String registration() {
        System.out.println("TESTING REGISTRATION");
        try {
            String request = requests.getRegistration();
            System.out.println(request);
            WebClient client = WebClient.builder().clientConnector(
                    new ReactorClientHttpConnector(HttpClient.create().followRedirect(true)))
                    .build();
            Mono<String> response = client.get()
                    .uri(request)
                    .retrieve()
                    .bodyToMono(String.class);
            return response.block();
        } catch (IOException e) {
            System.out.println("FAIL: \n" + e.getMessage());
        }
        return "";
    }
}
