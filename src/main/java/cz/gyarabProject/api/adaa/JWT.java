package cz.gyarabProject.api.adaa;

import cz.gyarabProject.api.datatype.Deployment;
import cz.gyarabProject.api.datatype.KeyHolder;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Properties;

import static cz.gyarabProject.api.Helper.*;

public class JWT {
    private final String separator;
    private final Properties props;
    private final KeyHolder keyHolder;

    public JWT(Properties props, KeyHolder keyHolder) {
        this.props = props;
        this.separator = props.getProperty("array.separator");
        this.keyHolder = keyHolder;
    }

    public String createNewTlsAuthentication(Deployment deployment) throws IOException, InterruptedException {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(getUri(deployment)));

        builder.header("x-correlation-id", props.getProperty("header.value.x-correlation-id"));
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
            return getAbsolutePath(props, "kb.uri.jwt.sanbox");
        } else if (deployment == Deployment.PRODUCTION) {
            return getAbsolutePath(props, "kb.uri.jwt.production");
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
                props.getProperty("application.name"),
                props.getProperty("application.name.en"),
                props.getProperty("application.version"),
                props.getProperty("application.uri"),
                fromStringToArray(props.getProperty("redirect.uri"), separator),
                props.getProperty("registration.back.uri"),
                fromStringToArray(props.getProperty("contacts"), separator, "email: "),
                props.getProperty("logo.uri"),
                props.getProperty("tos.uri"),
                props.getProperty("policy.uri")
        );
        if (!isValidJson(body)) {
            throw new IOException("body is invalid JSON");
        }
        return body;
    }
}
