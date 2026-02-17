package cz.gyarabProject.api.kb.adaa;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.gyarabProject.api.ObjectMappers;
import cz.gyarabProject.api.Property;
import cz.gyarabProject.api.kb.datatype.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component
public class AccountRequest {
    private final HttpClient httpClient;
    private final Property props;
    private final KeyHolder keyHolder;
    private final ObjectMapper mapper;
    private static final Property.Bank bank = Property.Bank.KB;

    public AccountRequest(HttpClient httpClient,
                          Property props,
                          KeyHolder keyHolder,
                          ObjectMappers mappers) {
        this.httpClient = httpClient;
        this.props = props;
        this.keyHolder = keyHolder;
        this.mapper = mappers.getMapper();
    }

    /**
     * Request for list of {@link AccountInfo}.
     *
     * @param token {@link AccessToken} for authorizing the request.
     * @return {@link AccountInfo[]} from kb.
     * @throws IOException When the request is unintentionaly shutted down or when is problem with maaping JSON to AccountInfo.
     * @throws InterruptedException When the request is interrupted.
     */
    public AccountInfo[] request(AccessToken token) throws IOException, InterruptedException {
        HttpRequest.Builder builder = HttpRequest.newBuilder().uri(
                props.getUri(bank, Property.Environment.SANDBOX, "account")
        );
        builder.header("x-correlation-id", props.get("x-correlation-id"));
        builder.header("apiKey", keyHolder.getApi());
        builder.header("Content-Type", "application/json");
        builder.header("Authorization", token.getTypeToken());

        HttpResponse<String> response = httpClient
                .send(builder.build(), HttpResponse.BodyHandlers.ofString());

        return mapper.readValue(response.body(), AccountInfo[].class);
    }
}
