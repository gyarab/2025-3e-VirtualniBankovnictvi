package cz.gyarabProject.api.adaa;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.gyarabProject.api.datatype.AccessToken;
import cz.gyarabProject.api.datatype.AccountInfo;
import cz.gyarabProject.api.datatype.KeyHolder;
import cz.gyarabProject.api.datatype.Property;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Component
public class AccountRequest {
    private final Property props;
    private final KeyHolder keyHolder;

    public AccountRequest(Property props, KeyHolder keyHolder) {
        this.props = props;
        this.keyHolder = keyHolder;
    }

    public AccountInfo[] request(AccessToken token) throws IOException, InterruptedException {
        HttpRequest.Builder builder = HttpRequest.newBuilder().uri(URI.create(props.getAbsolutePath("kb.uri", "kb.uri.account")));
        builder.header("x-correlation-id", props.get("header.value.x-correlation-id"));
        builder.header("apiKey", keyHolder.getApi());
        builder.header("Content-Type", "application/json");
        builder.header("Authorization", token.getTypeToken());

        HttpResponse<String> response = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(20))
                .build()
                .send(builder.build(), HttpResponse.BodyHandlers.ofString());

        return new ObjectMapper().readValue(response.body(), AccountInfo[].class);
    }
}
