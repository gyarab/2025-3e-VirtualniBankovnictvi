package cz.gyarabProject.api.kb.adaa;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.gyarabProject.api.kb.datatype.AccessToken;
import cz.gyarabProject.api.kb.datatype.KeyHolder;
import cz.gyarabProject.api.ObjectMappers;
import cz.gyarabProject.api.Property;
import cz.gyarabProject.database.kb.entity.Balance;
import cz.gyarabProject.database.kb.entity.BalanceDto;
import org.springframework.stereotype.Component;


import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component
public class BalanceRequest {
    private final HttpClient httpClient;
    private final Property props;
    private final KeyHolder keyHolder;
    private final ObjectMapper mapper;
    private static final Property.Bank bank = Property.Bank.KB;

    public BalanceRequest(HttpClient httpClient,
                          Property props,
                          KeyHolder keyHolder,
                          ObjectMappers mappers) {
        this.httpClient = httpClient;
        this.props = props;
        this.keyHolder = keyHolder;
        this.mapper = mappers.getDecimalMapper();
    }

    /**
     * Request for {@link Balance} of specific account.
     *
     * @param accountId ID of account whose {@link Balance} it is.
     * @param accessToken Token to autorize the request.
     * @return {@link Balance} of {@code accountId}.
     * @throws IOException When the request is unintentionaly shutted down or when is problem with maaping JSON to AccountInfo.
     * @throws InterruptedException When the request is interrupted.
     */
    public Balance getBalance(String accountId, AccessToken accessToken) throws IOException, InterruptedException {
        HttpRequest.Builder builder = HttpRequest.newBuilder();
        builder.uri(props.getUriWithEnding(bank, Property.Environment.SANDBOX, "account", accountId, "balances"));
        builder.header("x-correlation-id", props.get(bank, "x-correlation-id"));
        builder.header("apiKey", keyHolder.getApi());
        builder.header("Authorization", accessToken.getTypeToken());

        HttpResponse<String> response = httpClient
                .send(builder.build(), HttpResponse.BodyHandlers.ofString());

        return new Balance(accountId, mapper.readValue(response.body(), BalanceDto.class));
    }
}
