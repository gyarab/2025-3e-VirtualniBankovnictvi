package cz.gyarabProject.api.kb.adaa;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.gyarabProject.api.ObjectMappers;
import cz.gyarabProject.api.Property;
import cz.gyarabProject.api.kb.datatype.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;

public class StatementRequest {
    private final KeyHolder keyHolder;
    private final Property props;
    private final HttpClient client;
    private final ObjectMapper mapper;

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
        builder.uri(URI.create(props.getStatementUri(bankAccountId, from)));
        builder.header("x-correlation-id", props.get("x-correlation-id"));
        builder.header("apiKey", keyHolder.getApi());
        builder.header("Authorization", token.getTypeToken());

        HttpResponse<String> response = client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
        return mapper.readValue(response.body(), Statement[].class);
    }
}
