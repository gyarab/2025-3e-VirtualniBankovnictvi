package cz.gyarabProject.api.adaa;

import cz.gyarabProject.api.datatype.Deployment;
import cz.gyarabProject.api.datatype.KeyHolder;
import cz.gyarabProject.api.datatype.Property;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import static cz.gyarabProject.api.Helper.*;

@Component
public class JwtRequest {
    private final String separator;
    private final Property props;
    private final KeyHolder keyHolder;

    public JwtRequest(Property props, KeyHolder keyHolder) {
        this.props = props;
        this.separator = props.get("array.separator");
        this.keyHolder = keyHolder;
    }

    public String getNewJwt(Deployment deployment) throws IOException, InterruptedException {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(getUri(deployment)));

        builder.header("x-correlation-id", props.get("header.value.x-correlation-id"));
        builder.header("apiKey", keyHolder.getApi());
        builder.header("Content-Type", "application/json");

        builder.POST(HttpRequest.BodyPublishers.ofString(getBodyJson()));
        HttpResponse<String> response = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(20))
                .build()
                .send(builder.build(), HttpResponse.BodyHandlers.ofString());

        return response.body();
    }

    private String getUri(Deployment deployment) {
        if (deployment == Deployment.SANDBOX) {
            return props.getAbsolutePath("kb.uri.jwt.sandbox");
        } else if (deployment == Deployment.PRODUCTION) {
            return props.getAbsolutePath("kb.uri.jwt.production");
        } else {
            throw new IllegalStateException("Unsupported deployment type");
        }
    }

    private String getBodyJson() throws IOException {
        String body = String.format("""
                {
                  "softwareName": "%s",
                  "softwareNameEn": "%s",
                  "softwareId": "f64bf2e447e545228c78e07b091a82ee",
                  "softwareVersion": "%s",
                  "softwareUri": "%s",
                
                  "redirectUris": %s,
                
                  "tokenEndpointAuthMethod": "client_secret_post",
                
                  "grantTypes": [
                    "authorization_code"
                  ],
                
                  "responseTypes": [
                    "code"
                  ],
                
                  "registrationBackUri": "%s",
                  "contacts": %s,
                  "logoUri": "%s",
                  "tosUri": "%s",
                  "policyUri": "%s"
                }""",
                props.get("application.name"),
                props.get("application.name.en"),
                props.get("application.version"),
                props.get("application.uri"),
                fromStringToArray(props.get("redirect.uri"), separator),
                props.get("registration.back.uri"),
                fromStringToArray(props.get("contacts"), separator, "email: "),
                props.get("logo.uri"),
                props.get("tos.uri"),
                props.get("policy.uri")
        );
        if (!isValidJson(body)) {
            throw new IOException("body is invalid JSON");
        }
        return body;
    }
}
