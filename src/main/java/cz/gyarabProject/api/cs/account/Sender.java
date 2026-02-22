package cz.gyarabProject.api.cs.account;

import cz.gyarabProject.api.Property;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

public class Sender {
    private final Property props;
    private final HttpClient client;
    private static final Property.Bank bank = Property.Bank.CS;

    public Sender(Property props, HttpClient client) {
        this.props = props;
        this.client = client;
    }

    protected HttpResponse<String> send(URI uri, Map<String, String> headers) throws IOException, InterruptedException {
        HttpRequest.Builder builder = HttpRequest.newBuilder();
        builder.header("WEB-API-key", props.get(bank, "api-key"));

        headers.forEach(builder::header); // Same as headers.forEach((k, v) -> builder.header(k, v));

        return client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
    }

    protected HttpResponse<String> send(URI uri) throws IOException, InterruptedException {
        return send(uri, Map.of());
    }
}
