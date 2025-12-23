package cz.gyarabProject.api.adaa;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.gyarabProject.api.datatype.AccessToken;
import cz.gyarabProject.api.datatype.AccountInfo;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Properties;

import static cz.gyarabProject.api.Helper.getAbsolutePath;
import static cz.gyarabProject.api.Helper.readValidFile;

public class Account {
    private final Properties props;

    public Account(Properties props) {
        this.props = props;
    }

    public AccountInfo[] request(AccessToken token) throws IOException, InterruptedException {
        HttpRequest.Builder builder = HttpRequest.newBuilder().uri(URI.create("https://api-gateway.kb.cz/adaa/v2/accounts"));
        builder.header("x-correlation-id", props.getProperty("header.value.x-correlation-id"));
        builder.header("apiKey", getKey());
        builder.header("Content-Type", "application/json");
        builder.header("Authorization", token.getTypeToken());

        HttpResponse<String> response = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(20))
                .build()
                .send(builder.build(), HttpResponse.BodyHandlers.ofString());

        return new ObjectMapper().readValue(response.body(), AccountInfo[].class);
    }

    private String getKey() throws IOException {
        return readValidFile(getAbsolutePath(props, "key-token.path", "key.path"));
    }
}
