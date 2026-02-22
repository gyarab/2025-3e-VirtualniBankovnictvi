package cz.gyarabProject.api.cs;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.gyarabProject.api.ObjectMappers;
import cz.gyarabProject.api.Property;
import cz.gyarabProject.api.cs.account.Sender;
import cz.gyarabProject.api.cs.datatype.place.ATM;
import cz.gyarabProject.api.cs.datatype.PageInfo;
import cz.gyarabProject.api.cs.datatype.Region;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
public class Places extends Sender {
    private final Property props;
    private final ObjectMapper mapper;

    public enum Detail {
        MINIMAL, NORMAL, FULL
    }

    public Places(Property props, ObjectMappers mappers) {
        this.props = props;
        this.mapper = mappers.getMapper();
    }

    public PageInfo<ATM> atms(
            String place, String lat1, String lat2, String lng1, String lng2, String radius, Region region,
            String country, String flag, String bankCode, int page, int size, String sort, Detail detail,
            LocalDate openTime
    ) throws IOException, InterruptedException {
        URI uri;
        Map<String, Object> rawQuery = new HashMap<>(Map.of(
                "radius", radius,
                "region", region.name().replace('_', ' '),
                "country", country,
                "flags", flag,
                "bankCode", bankCode,
                "page", page,
                "size", size,
                "sort", sort,
                "detail", detail,
                "openTime", openTime
        ));
        if (lat2 == null || lng2 == null) {
            rawQuery.putAll(Map.of("q", place, "lat", lat1, "lng", lng1));
            String query = props.buildQuery(rawQuery);
            uri = props.getUri(bank(), Property.Environment.SANDBOX, "atm", query, "atms");
        } else {
            rawQuery.putAll(
                    Map.of("q", place, "lat1", lat1, "lat2", lat2, "lng1", lng1, "lng2", lng2)
            );
            String query = props.buildQuery(rawQuery);
            uri = props.getUri(bank(), Property.Environment.SANDBOX, "atm", query, "atms", "within");
        }

        HttpResponse<String> response = send(uri);
        return mapper.readValue(response.body(), new TypeReference<>() {});
    }

    public PageInfo<ATM> atms(
            String place, String lat, String lng, String radius, Region region, String country, String flag,
            String bankCode, int page, int size, String sort, Detail detail, LocalDate openTime
    ) throws IOException, InterruptedException {
        return atms(place, lat, null, lng, null, radius, region, country, flag, bankCode,
                page, size, sort, detail, openTime);
    }

    public ATM atms(int id) throws IOException, InterruptedException {
        HttpResponse<String> response = send(
                props.getUriWithEnding(bank(), Property.Environment.SANDBOX, "atm",
                        "atms", Integer.toString(id)
                )
        );
        return mapper.readValue(response.body(), ATM.class);
    }

    public List<String> services(int id) throws IOException, InterruptedException {
        HttpResponse<String> response = send(
                props.getUriWithEnding(bank(), Property.Environment.SANDBOX, "atm",
                        "atms", Integer.toString(id), "services"
                )
        );
        return mapper.readValue(response.body(), new TypeReference<>() {});
    }

    public ATM.Flag flags() throws IOException, InterruptedException {
        HttpResponse<String> response = send(
                props.getUriWithEnding(bank(), Property.Environment.SANDBOX, "atm", "atms")
        );
        return mapper.readValue(response.body(), ATM.Flag.class);
    }

    public String photos(int id) throws IOException, InterruptedException {
        HttpResponse<String> response = send(
                props.getUriWithEnding(bank(), Property.Environment.SANDBOX,
                        "atm", "atms", Integer.toString(id), "photos"
                )
        );
        return switch (response.statusCode() / 100) {
            case 5 -> "Sorry this is not Implemented yet from bank site.";
            case 2 -> "Sorry this is not implemented yet.";
            default -> "Something went wrong.";
        };
    }
}
