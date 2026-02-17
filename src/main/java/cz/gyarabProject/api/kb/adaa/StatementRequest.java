package cz.gyarabProject.api.kb.adaa;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.gyarabProject.api.ObjectMappers;
import cz.gyarabProject.api.Property;
import cz.gyarabProject.api.kb.datatype.*;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.Map;

public class StatementRequest {
    private final KeyHolder keyHolder;
    private final Property props;
    private final HttpClient client;
    private final ObjectMapper mapper;
    private static final Property.Bank bank = Property.Bank.KB;

    public StatementRequest(KeyHolder keyHolder,
                            Property props,
                            HttpClient client,
                            ObjectMappers mappers) {
        this.keyHolder = keyHolder;
        this.props = props;
        this.client = client;
        this.mapper = mappers.getMapper();
    }

    public Statement[] request(String bankAccountId, Instant from, AccessToken token) throws IOException, InterruptedException {
        HttpRequest.Builder builder = HttpRequest.newBuilder();
        String query = props.buildQuery(Map.of("fromDate", from));
        builder.uri(props.getUri(bank, Property.Environment.SANDBOX, "account", query,
                bankAccountId, "statement"));
        builder.header("x-correlation-id", props.get("x-correlation-id"));
        builder.header("apiKey", keyHolder.getApi());
        builder.header("Authorization", token.getTypeToken());

        HttpResponse<String> response = client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
        return mapper.readValue(response.body(), Statement[].class);
    }
}
