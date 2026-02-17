package cz.gyarabProject.api.kb.adaa;

import cz.gyarabProject.api.kb.datatype.AccessToken;
import cz.gyarabProject.api.kb.datatype.KeyHolder;
import cz.gyarabProject.api.Property;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static cz.gyarabProject.api.Helper.isValidJson;

@Component
public class Subscription {
    private final Property props;
    private final HttpClient client;
    private final KeyHolder keyHolder;
    private static final Property.Bank bank = Property.Bank.KB;

    public Subscription(Property props,
                        HttpClient client,
                        KeyHolder keyHolder) {
        this.props = props;
        this.client = client;
        this.keyHolder = keyHolder;
    }

    public void create(String bankAccountId, AccessToken token, String key) throws IOException, InterruptedException {
        if (key.equals("DELETE")) {
            throw new IllegalArgumentException("Key cannot be DELETE");
        }
        getRequest(bankAccountId, token, key);
    }

    public String getInfo(String bankAccountId, AccessToken token) throws IOException, InterruptedException {
        return getRequest(bankAccountId, token, "");
    }

    public void unsubscribe(String bankAccountId, AccessToken token) throws IOException, InterruptedException {
        getRequest(bankAccountId, token, "DELETE");
    }

    private String getRequest(String bankAccountId, AccessToken token, String key) throws IOException, InterruptedException {
        HttpRequest.Builder builder = HttpRequest.newBuilder();
        builder.uri(props.getUriWithEnding(bank, Property.Environment.SANDBOX, "account",
                bankAccountId, "transactions/event-subscription"));

        builder.header("x-correlation-id", props.get("x-correlation-id"));
        builder.header("apiKey", keyHolder.getApi());
        builder.header("Authorization", token.getTypeToken());

        if (key.equals("DELETE")) {
            builder.DELETE();
        } else if (key.isBlank()) {
            builder.header("Content-Type", "application/json");
            builder.POST(getBodyPublisher(key));
        }

        HttpResponse<String> response = client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    private HttpRequest.BodyPublisher getBodyPublisher(String key) throws IOException {
        String body = String.format("""
                {
                "eventApiUrl": "%s",
                "eventApiKey": "%s"
                }
                """,
                props.get("subscription.uri"),
                key);
        if (!isValidJson(body))
        {
            throw new IOException("Body is invalid JSON");
        }
        return HttpRequest.BodyPublishers.ofString(body);
    }
}
