package cz.gyarabProject.api.kb.adaa;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.gyarabProject.api.kb.datatype.token.AccessToken;
import cz.gyarabProject.api.kb.datatype.KeyHolder;
import cz.gyarabProject.api.ObjectMappers;
import cz.gyarabProject.api.Property;
import cz.gyarabProject.database.kb.entity.Transaction;
import cz.gyarabProject.database.kb.entity.TransactionDto;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.Iterator;
import java.util.Map;

@Component
public class TransactionRequest {
    private final HttpClient httpClient;
    private final Property props;
    private final KeyHolder keyHolder;
    private final ObjectMapper mapper;
    private static final Property.Bank bank = Property.Bank.KB;

    public TransactionRequest(HttpClient httpClient, Property props, KeyHolder keyHolder, ObjectMappers mappers) {
        this.httpClient = httpClient;
        this.props = props;
        this.keyHolder = keyHolder;
        this.mapper = mappers.getDecimalMapper();
    }

    /**
     * Request for {@link Transaction[]} of a specific account.
     *
     * @param bankAccountId ID of account whose Transaction it will be.
     * @param token {@link AccessToken} for authorizing the request.
     * @param from {@link Instant} from what date the {@link Transaction[]} will be.
     * @param to {@link Instant} to what date the {@link Transaction[]} will be.
     * @return {@link Transaction[]} of specific time interval and account.
     * @throws IOException When the request is unintentionaly shutted down or when is problem with maaping JSON to AccountInfo.
     * @throws InterruptedException When the request is interrupted.
     */
    public Transaction[] getTransaction(String bankAccountId, AccessToken token, Instant from, Instant to) throws IOException, InterruptedException {
        HttpRequest.Builder builder = HttpRequest.newBuilder();
        String query = props.buildQuery(Map.of("account", bankAccountId,
                "from", from,
                "to", to,
                "size", 100,
                "page", 0)
        );
        builder.uri(props.getUri(bank, Property.Environment.SANDBOX, "account", query,
                bankAccountId, "transactions"));
        builder.header("x-correlation-id", props.get(bank, "x-correlation-id"));
        builder.header("apiKey", keyHolder.getApi());
        builder.header("Authorization", token.getTypeToken());

        HttpResponse<String> response = httpClient
                .send(builder.build(), HttpResponse.BodyHandlers.ofString());
        return map(bankAccountId, response.body());
    }

    /**
     * Mappes the returned JSON to {@link Transaction[]}.
     *
     * @param bankAccountId ID of account whose {@link Transaction[]} it is.
     * @param json {@link String} containing valid JSON.
     * @return Mapped {@link Transaction[]}.
     * @throws JsonProcessingException When the {@code json} is invalid JSON.
     */
    private Transaction[] map(String bankAccountId, String json) throws JsonProcessingException {
        Iterator<JsonNode> nodes = mapper.readTree(json).get("content").elements();
        Transaction[] transactions = new Transaction[count(nodes)];
        int index = 0;
        for (; nodes.hasNext(); index++) {
            transactions[index] = new Transaction(bankAccountId, mapper.treeToValue(nodes.next(), TransactionDto.class));
        }
        return transactions;
    }

    /**
     * Count the size of {@link Iterator}.
     *
     * @param it {@link Iterator} to be counted.
     * @return Int that is size of the {@link Iterator}.
     */
    private int count(Iterator<?> it) {
        int size = 0;
        while (it.hasNext()) {
            size++;
            it.next();
        }
        return size;
    }
}
