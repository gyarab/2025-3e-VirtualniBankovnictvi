package cz.gyarabProject.api.sporitelna;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.gyarabProject.api.ObjectMappers;
import cz.gyarabProject.api.Property;
import cz.gyarabProject.api.sporitelna.datatype.Currency;
import cz.gyarabProject.api.sporitelna.datatype.ExchangeRate;
import cz.gyarabProject.api.sporitelna.datatype.ExchangedCurrency;
import cz.gyarabProject.api.sporitelna.datatype.Language;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class ExchangeRates {
    private final Property props;
    private final HttpClient client;
    private final ObjectMapper mapper;
    private static final String uri = "/exchangerates/";

    public ExchangeRates(Property props, HttpClient client, ObjectMappers mappers) {
        this.props = props;
        this.client = client;
        this.mapper = mappers.getMapper();
    }

    public ExchangeRate[] exchangeRates(LocalDate from, LocalDate to, String currency, Language lang,
                                      boolean card) throws IOException, InterruptedException {
        HttpRequest.Builder builder = HttpRequest.newBuilder();
        String query = "?fromDate=" + from.toString() + "&toDate=" + to.toString() + "&curr="
                + currency + "&lang=" + lang.name();
        builder.uri(URI.create(props.get("exchange.sandbox") + uri + (card ? "/card" : "") + query));
        builder.header("WEB-API-key", props.get("api.key"));

        HttpResponse<String> response = client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
        return mapper.readValue(response.body(), ExchangeRate[].class);
    }

    public ExchangedCurrency exchange(String from, String to, String type, double amount,
                                      boolean buy) throws IOException, InterruptedException {
        HttpRequest.Builder builder = HttpRequest.newBuilder().uri(URI.create(props.get("exchange.sandbox") + uri));
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
                buy)));

        HttpResponse<String> response = client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
        return mapper.readValue(response.body(), ExchangedCurrency.class);
    }

    public Currency[] getCurrencies(Language lang, boolean card) throws IOException, InterruptedException {
        HttpRequest.Builder builder = HttpRequest.newBuilder().uri(URI.create(
                props.get("exchange.sandbox") + uri + (card ? "card/" : "") + "currencies?lang=" + lang.name()));
        builder.header("WEB-API-key", props.get("api.key"));

        HttpResponse<String> response = client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
        return mapper.readValue(response.body(), Currency[].class);
    }

    public ExchangeRate[] cross(LocalDate from, LocalDate to, String currFrom, String currTo) throws IOException, InterruptedException {
        HttpRequest.Builder builder = HttpRequest.newBuilder();
        String query = "?fromDate=" + from.toString() + "&toDate=" + to.toString() + "&currency1="
                + currFrom + "&currency2=" + currTo;
        builder.uri(URI.create(props.get("exchange.sandbox") + uri + "cross" + query));
        builder.header("WEB-API-key", props.get("api.key"));

        HttpResponse<String> response = client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
        return mapper.readValue(response.body(), ExchangeRate[].class);
    }

    public LocalDateTime[] times(LocalDate date) throws IOException, InterruptedException {
        HttpRequest.Builder builder = HttpRequest.newBuilder().uri(URI.create(
                props.get("exchange.sandbox") + uri + "times?date=" + date));
        builder.header("WEB-API-key", props.get("api.key"));

        HttpResponse<String> response = client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
        return mapper.treeToValue(mapper.readTree(response.body()).get("date"), LocalDateTime[].class);
    }

    public boolean isAvailable() throws IOException, InterruptedException {
        HttpRequest.Builder builder = HttpRequest.newBuilder().uri(URI.create(
                props.get("exchange.sandbox") + "/health"));
        builder.header("WEB-API-key", props.get("api.key"));

        HttpResponse<String> response = client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
        return 200 <= response.statusCode() && response.statusCode() < 300;
    }
}
