package cz.gyarabProject.api.sporitelna;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.gyarabProject.api.ObjectMappers;
import cz.gyarabProject.api.Property;
import cz.gyarabProject.api.sporitelna.datatype.ATM;
import cz.gyarabProject.api.sporitelna.datatype.PageInfo;
import cz.gyarabProject.api.sporitelna.datatype.Region;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;


@Component
public class Places {
    private final Property props;
    private final HttpClient client;
    private final ObjectMapper mapper;

    public enum Detail {
        MINIMAL, NORMAL, FULL
    }

    public Places(Property props, HttpClient client, ObjectMappers mappers) {
        this.props = props;
        this.client = client;
        this.mapper = mappers.getMapper();
    }

    private String buildQuery(Object... params) {
        return buildQuery("?", params);
    }

    private String buildQuery(String string, Object... params) {
        if (params.length % 2 != 0) {
            throw new IllegalArgumentException("Key have to have its value. Params is not even!");
        }
        StringBuilder builder = new StringBuilder(string);
        for (int i = 0; i < params.length; i += 2) {
            builder.append(params[i].toString()).append("=").append(params[i + 1].toString()).append("&");
        }
        builder.deleteCharAt(builder.length() - 1);
        return builder.toString();
    }

    private HttpResponse<String> send(URI uri) throws IOException, InterruptedException {
        HttpRequest.Builder builder = HttpRequest.newBuilder().uri(uri);
        builder.header("WEB-API-key", props.get("api.key"));

        return client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
    }

    public PageInfo<ATM> atms(String place, String lat1, String lat2, String lng1, String lng2, String radius,
                              Region region, String country, String flag, String bankCode, int page, int size,
                              String sort, Detail detail, LocalDate openTime) throws IOException, InterruptedException {
        String uri;
        if (lat2 == null && lng2 == null) {
            uri = buildQuery(props.get("atm.sandbox") + "/atms?", "q", place, "lat", lat1, "lng", lng1);
        } else {
            uri = buildQuery(props.get("atm.sandbox") + "/atms/withn?", "q", place, "lat1", lat1, "lat2",
                    lat2, "lng1", lng1, "lng2", lng2);
        }
        uri = buildQuery(uri, "radius", radius, "region", region.name().replace('_', ' '),
                "country", country, "flags", flag, "bankCode", bankCode, "page", page, "size", size, "sort",
                sort, "detail", detail.name(), "openTime", openTime
        );

        HttpResponse<String> response = send(URI.create(uri));
        return mapper.readValue(response.body(), new TypeReference<>() {});
    }

    public PageInfo<ATM> atms(String place, String lat, String lng, String radius, Region region, String country,
                              String flag, String bankCode, int page, int size, String sort, Detail detail,
                              LocalDate openTime) throws IOException, InterruptedException {
        return atms(place, lat, null, lng, null, radius, region, country, flag, bankCode,
                page, size, sort, detail, openTime);
    }

    public ATM atms(int id) throws IOException, InterruptedException {
        HttpResponse<String> response = send(URI.create(props.get("atm.sandbox") + "/atms/" + id));
        return mapper.readValue(response.body(), ATM.class);
    }

    public String[] services(int id) throws IOException, InterruptedException {
        HttpResponse<String> response = send(URI.create(props.get("atm.sandbox") + "/atms/" + id + "/services"));
        return mapper.readValue(response.body(), String[].class);
    }

    public ATM.Flag flags() throws IOException, InterruptedException {
        HttpResponse<String> response = send(URI.create(props.get("atm.sandbox") + "/atms/"));
        return mapper.readValue(response.body(), ATM.Flag.class);
    }

    public String photos(int id) throws IOException, InterruptedException {
        HttpRequest.Builder builder = HttpRequest.newBuilder().uri(URI.create(props.get("atm.sandbox") +
                "/atms/" + id + "/photos"));

        HttpResponse<String> response = client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
        return switch (response.statusCode() / 100) {
            case 5 -> "Sorry this is not Implemented yet from bank site.";
            case 2 -> "Sorry this is not implemented yet.";
            default -> "Something went wrong.";
        };
    }
}
