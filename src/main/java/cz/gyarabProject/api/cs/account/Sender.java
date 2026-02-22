package cz.gyarabProject.api.cs.account;

import cz.gyarabProject.api.Property;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

@Component
public class Sender {
    @Autowired
    private Property props;
    @Autowired
    private HttpClient client;
    private static final Property.Bank bank = Property.Bank.CS;

    protected HttpResponse<String> send(URI uri, Map<String, String> headers) throws IOException, InterruptedException {
        HttpRequest.Builder builder = HttpRequest.newBuilder();
        builder.header("WEB-API-key", props.get(bank, "api-key"));

        headers.forEach(builder::header); // Same as headers.forEach((k, v) -> builder.header(k, v));

        return client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
    }

    protected HttpResponse<String> send(URI uri) throws IOException, InterruptedException {
        return send(uri, Map.of());
    }

    protected Property.Bank bank() {
        return bank;
    }
}
