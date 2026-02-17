package cz.gyarabProject.api.kb.adaa;

import cz.gyarabProject.api.kb.datatype.KeyHolder;
import cz.gyarabProject.api.Property;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static cz.gyarabProject.api.Helper.*;

@Component
public class JwtRequest {
    private final HttpClient httpClient;
    private final Property props;
    private final String separator;
    private final KeyHolder keyHolder;
    private static final Property.Bank bank = Property.Bank.KB;

    public JwtRequest(HttpClient httpClient, Property props, KeyHolder keyHolder) {
        this.httpClient = httpClient;
        this.props = props;
        this.separator = props.get("array.separator");
        this.keyHolder = keyHolder;
    }

    /**
     * Generate new JWT and return it as {@link String}.
     *
     * @return New JWT as {@link String}.
     * @throws IOException When the request is unintentionaly shutted down or when is problem with maaping JSON to AccountInfo.
     * @throws InterruptedException When the request is interrupted.
     */
    public String getNewJwt() throws IOException, InterruptedException {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(getUri(keyHolder.getEnvironment()));

        builder.header("x-correlation-id", props.get("x-correlation-id"));
        builder.header("apiKey", keyHolder.getApi());
        builder.header("Content-Type", "application/json");

        builder.POST(HttpRequest.BodyPublishers.ofString(getBodyJson()));
        HttpResponse<String> response = httpClient
                .send(builder.build(), HttpResponse.BodyHandlers.ofString());

        return response.body();
    }

    /**
     * Return URI as {@link String} to KB JWT generator based on which {@link Property.Environment} the request is.
     *
     * @param environment {@link Property.Environment} where the URI goes.
     * @return {@link String} that is URI for KB JWT generator.
     */
    private URI getUri(Property.Environment environment) {
        if (environment == Property.Environment.SANDBOX) {
            return props.getUri(bank, Property.Environment.SANDBOX, "jwt");
        } else if (environment == Property.Environment.PRODUCTION) {
            return props.getUri(bank, Property.Environment.SANDBOX, "jwt");
        } else {
            throw new IllegalStateException("Unsupported environment type");
        }
    }

    /**
     * Generates a body for JWT request that is {@link String} as valid JSON.
     *
     * @return {@link String} as a valid JSON.
     * @throws IOException When the request is invalid JSON.
     */
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
