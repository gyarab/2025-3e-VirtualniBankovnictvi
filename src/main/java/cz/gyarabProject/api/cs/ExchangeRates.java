package cz.gyarabProject.api.cs;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.gyarabProject.api.ObjectMappers;
import cz.gyarabProject.api.Property;
import cz.gyarabProject.api.cs.datatype.Currency;
import cz.gyarabProject.api.cs.datatype.ExchangeRate;
import cz.gyarabProject.api.cs.datatype.ExchangedCurrency;
import cz.gyarabProject.api.cs.datatype.Language;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Component
public class ExchangeRates {
    private final Property props;
    private final HttpClient client;
    private final ObjectMapper mapper;
    private static final String uriEnding = "exchangerates";
    private static final Property.Bank bank = Property.Bank.CS;

    public ExchangeRates(Property props, HttpClient client, ObjectMappers mappers) {
        this.props = props;
        this.client = client;
        this.mapper = mappers.getMapper();
    }

    public List<ExchangeRate> exchangeRates(LocalDate from, LocalDate to, String currency, Language lang,
                                            boolean card) throws IOException, InterruptedException {
        HttpRequest.Builder builder = HttpRequest.newBuilder();
        String query = props.buildQuery(Map.of(
                "fromDate", from,
                "toDate", to,
                "curr", currency,
                "lang", lang
        ));
        builder.uri(props.getUri(bank, Property.Environment.SANDBOX,
                "exchange", query, uriEnding, card ? "/card" : ""));
        builder.header("WEB-API-key", props.get("api.key"));

        HttpResponse<String> response = client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
        return mapper.readValue(response.body(), new TypeReference<>() {});
    }

    public ExchangedCurrency exchange(String from, String to, String type, double amount,
                                      boolean buy) throws IOException, InterruptedException {
        HttpRequest.Builder builder = HttpRequest.newBuilder().uri(
                props.getUriWithEnding(bank, Property.Environment.SANDBOX, "exchange", uriEnding)
        );
        builder.header("Content-Type", "application/json;charset=UTF-8");
        builder.header("WEB-API-key", props.get("api.key"));
        builder.POST(HttpRequest.BodyPublishers.ofString(String.format("""
                {
                "from": "%s",
                "to": "%s",
                "type": "%s",
                "amount": "%s",
                "buy": "%s"
                }""",
                from,
                to,
                type,
                amount,
                buy)
        ));

        HttpResponse<String> response = client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
        return mapper.readValue(response.body(), ExchangedCurrency.class);
    }

    public List<Currency> getCurrencies(Language lang, boolean card) throws IOException, InterruptedException {
        HttpRequest.Builder builder = HttpRequest.newBuilder().uri(
                props.getUri(bank, Property.Environment.SANDBOX, "exchange.sandbox",
                        props.buildQuery(Map.of("lang", lang)),
                        uriEnding, card ? "card" : "", "currencies"
                )
        );
        builder.header("WEB-API-key", props.get("api.key"));

        HttpResponse<String> response = client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
        return mapper.readValue(response.body(), new TypeReference<>() {});
    }

    public List<ExchangeRate> cross(LocalDate from, LocalDate to, String currFrom,
                                String currTo) throws IOException, InterruptedException {
        HttpRequest.Builder builder = HttpRequest.newBuilder();
        String query = props.buildQuery(Map.of(
                "fromDate", from,
                "toDate", to,
                "currency1", currFrom,
                "currency2", currTo
        ));
        builder.uri(props.getUri(
                bank, Property.Environment.SANDBOX, "exchange", query, uriEnding, "cross"
        ));
        builder.header("WEB-API-key", props.get("api.key"));

        HttpResponse<String> response = client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
        return mapper.readValue(response.body(), new TypeReference<>() {});
    }

    public List<LocalDateTime> times(LocalDate date) throws IOException, InterruptedException {
        HttpRequest.Builder builder = HttpRequest.newBuilder().uri(
                props.getUri(bank, Property.Environment.SANDBOX, "exchange",
                        props.buildQuery(Map.of("date", date)), uriEnding, "times"
                )
        );
        builder.header("WEB-API-key", props.get("api.key"));

        HttpResponse<String> response = client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
        return mapper.treeToValue(mapper.readTree(response.body()).get("date"), new TypeReference<>() {});
    }

    public boolean isAvailable() throws IOException, InterruptedException {
        HttpRequest.Builder builder = HttpRequest.newBuilder().uri(
                props.getUriWithEnding(bank, Property.Environment.SANDBOX, "exchange", "health")
        );
        builder.header("WEB-API-key", props.get("api.key"));

        HttpResponse<String> response = client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
        return 200 <= response.statusCode() && response.statusCode() < 300;
    }
}
