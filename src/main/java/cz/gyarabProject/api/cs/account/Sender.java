package cz.gyarabProject.api.cs.account;

import cz.gyarabProject.api.Property;
import cz.gyarabProject.api.cs.datatype.Token;
import org.json.JSONObject;
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
    @Autowired private Property props;
    @Autowired private HttpClient client;
    private static final Property.Bank bank = Property.Bank.CS;

    public enum Method {
        GET, POST, PUT, HEAD, DELETE
    }

    protected HttpResponse<String> send(
            URI uri, Map<String, String> headers, Method method, JSONObject body, boolean bearer, Token token
    ) throws IOException, InterruptedException {
        HttpRequest.Builder builder = HttpRequest.newBuilder(uri);
        builder.header("WEB-API-key", props.get(bank, "api.key"));
        if (bearer) {
            builder.header("Authorization", "Bearer " + token.getAccess());
        }
        headers.forEach(builder::header); // Same as headers.forEach((k, v) -> builder.header(k, v));

        switch (method) {
            case GET:
            case null:
                break;
            case POST:
                builder.POST(HttpRequest.BodyPublishers.ofString(body.toString()));
                break;
            case PUT:
                builder.PUT(HttpRequest.BodyPublishers.ofString(body.toString()));
                break;
            case DELETE:
                builder.DELETE();
                break;
            case HEAD:
                builder.HEAD();
                break;
        }

        return client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
    }

    protected HttpResponse<String> send(
            URI uri, Map<String, String> headers, Method method, JSONObject body, Token token
    ) throws IOException, InterruptedException {
        return send(uri, headers, method, body, true, token);
    }

    protected HttpResponse<String> send(URI uri, Map<String, String> headers, Token token) throws IOException, InterruptedException {
        return send(uri, headers, Method.GET, null, true, token);
    }

    protected HttpResponse<String> send(URI uri, boolean bearer, Token token) throws IOException, InterruptedException {
        return send(uri, Map.of(), Method.GET, null, bearer, token);
    }

    protected HttpResponse<String> send(URI uri, Token token) throws IOException, InterruptedException {
        return send(uri, Map.of(), Method.GET, null, true, token);
    }

    protected Property.Bank bank() {
        return bank;
    }
}
