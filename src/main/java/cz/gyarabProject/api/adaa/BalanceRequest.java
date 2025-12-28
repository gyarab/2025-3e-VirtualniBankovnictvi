package cz.gyarabProject.api.adaa;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.gyarabProject.api.datatype.AccessToken;
import cz.gyarabProject.api.datatype.KeyHolder;
import cz.gyarabProject.api.datatype.Property;
import cz.gyarabProject.database.entity.Balance;
import org.springframework.stereotype.Component;


import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Component
public class BalanceRequest {
    private final Property props;
    private final KeyHolder keyHolder;

    public BalanceRequest(Property props, KeyHolder keyHolder) {
        this.props = props;
        this.keyHolder = keyHolder;
    }

    public Balance getBalance(String accountId, AccessToken accessToken) throws IOException, InterruptedException {
        HttpRequest.Builder builder = HttpRequest.newBuilder();
        builder.uri(URI.create(props.getAbsolutePath("kb.uri", "kb.uri.account") + "/" + accountId + "/balances"));
        builder.header("x-correlation-id", props.get("header.value.x-correlation-id"));
        builder.header("apiKey", keyHolder.getApi());
        builder.header("Authorization", accessToken.getTypeToken());

        HttpResponse<String> response = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(20))
                .build()
                .send(builder.build(), HttpResponse.BodyHandlers.ofString());

        return new Balance(accountId, new ObjectMapper().readValue(response.body(), Balance.Builder.class));
    }
}
