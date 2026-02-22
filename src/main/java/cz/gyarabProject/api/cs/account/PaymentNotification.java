package cz.gyarabProject.api.cs.account;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.gyarabProject.api.ObjectMappers;
import cz.gyarabProject.api.Property;
import cz.gyarabProject.api.cs.datatype.notification.NotificationInfo;
import cz.gyarabProject.api.cs.datatype.notification.Response;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

@RestController
public class PaymentNotification extends Sender {
    private final Property props;
    private final ObjectMapper mapper;
    private static final String URL = "/sporitelna/payment/";

    public enum Specification {
        card, account
    }

    public PaymentNotification(Property props, ObjectMappers mappers) {
        this.props = props;
        this.mapper = mappers.getMapper();
    }

    public NotificationInfo create(String id, Specification specification) throws IOException, InterruptedException {
        HttpResponse<String> response = send(
                props.getUriWithEnding(
                        bank(), Property.Environment.SANDBOX, "account",
                        "my", specification.name(), id, "notification"
                ),
                Map.of("Content-Type", "application.json"),
                Method.POST,
                new JSONObject().put(
                        "notificationUrl",
                        props.get("application.uri") + URL + specification.name() + "/transaction-notification"
                )
        );
        return mapper.readValue(response.body(), NotificationInfo.class);
    }

    public List<NotificationInfo> list(String id, Specification specification) throws IOException, InterruptedException {
        HttpResponse<String> response = send(props.getUriWithEnding(
                bank(), Property.Environment.SANDBOX, "account",
                "my", specification.name(), id, "notification"
        ));
        JsonNode node = mapper.readTree(response.body());
        return mapper.treeToValue(node.get("notification"), new TypeReference<>() {});
    }

    public void delete(String id, String notificationId, Specification specification) throws IOException, InterruptedException {
        HttpResponse<String> response = send(
                props.getUriWithEnding(
                        bank(), Property.Environment.SANDBOX, "account",
                        "my", specification.name(), id, "notification", notificationId
                )
        );
    }

    @GetMapping(URL + "{specification}" + "/transaction-notification")
    public void paymentNotification(
            @PathVariable Specification specification,
            @RequestHeader("fingerprint") String fingerprint,
            @RequestBody String body
    ) throws JsonProcessingException {
        Response response = mapper.readValue(body, Response.class);
        //logic here
    }
}
