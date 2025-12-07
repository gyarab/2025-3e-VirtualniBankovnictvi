import Decryption.ResponseDecryption;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.time.Duration;
import java.util.Arrays;
import java.util.Base64;
import java.util.Properties;

public class Requests {
    private final String keysTokensPath = "./../../../keys-tokens/";
    private final Properties props;
    private final String separator;

    public Requests(Properties props) {
        this.props = props;
        this.separator = props.getProperty("separator", ", ");
    }

    private String readFile(String path) {
        try {
            File file = new File(path);
            if (!file.exists()) return null;
            return Files.readString(file.toPath());
        } catch (Exception e) {
            return null;
        }
    }

    private void createAndWriteFile(String path, String content) throws IOException {
        File file = new File(path);
        if (!file.exists()) {
            file.createNewFile();
            new FileOutputStream(file).write(content.getBytes());
            return;
        }
        throw new IOException("File allready exists.");
    }

    public void createNewTlsAuthentication() throws IOException, InterruptedException {
        HttpRequest.Builder request = HttpRequest.newBuilder()
                .uri(URI.create(props.getProperty("sandbox.uri")));

        request.header("x-correlation-id", props.getProperty("header.value.x-correlation-id"));
        String key = readFile(keysTokensPath + props.getProperty("header.value.api-key.path"));
        if (key == null || key.isEmpty()) {
            throw new IOException("header.value.api-key.path is empty or doesn't exist");
        }
        request.header("apiKey", key);
        request.header("Content-Type", props.getProperty("header.value.Content-Type"));

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
                    "authorization_code",
                    "refresh_token"
                  ],
                
                  "responseTypes": [
                    "code"
                  ],
                
                  "registrationBackUri": "https://client.example.org/backuri",
                }""",
                props.getProperty("application.name"),
                props.getProperty("application.name.en"),
                props.getProperty("application.version"),
                props.getProperty("application.uri"),
                Arrays.toString(props.getProperty("redirect.uri").split(separator)));

        request.POST(HttpRequest.BodyPublishers.ofString(body));
        HttpResponse<String> response = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(20))
                .build()
                .send(request.build(), HttpResponse.BodyHandlers.ofString());

        createAndWriteFile(keysTokensPath + props.getProperty("jwt.path"), response.body());
    }

    public String GetRegistration(boolean isProduction) throws IOException, InterruptedException {
        String jsonRequest;
        String jsonByte64;
        if (isProduction) {
            String jwt = readFile(keysTokensPath + props.getProperty("jwt.path"));
            if (jwt == null || jwt.isEmpty()) {
                throw new IOException("jwt.path is empty or doesn't exist");
            }
            jsonRequest = String.format("""
                    {
                      "clientName": "%s",
                      "clientNameEn": "%s",
                      "applicationType": "%s",
                      "redirectUris": %s,
                      "scope": %s,
                      "encryptionKey": "MnM1djh5L0I/RShIK01iUWVUaFdtWnEzdDZ3OXokQyY=",
                      "encryptionAlg": "AES-256",
                      "softwareStatement": "%s"
                    }""",
                    props.getProperty("application.name"),
                    props.getProperty("application.name.en"),
                    props.getProperty("application.type"),
                    props.getProperty("redirect.uri"),
                    Arrays.toString(props.getProperty("api.scopes").split(separator)),
                    jwt
            );
        } else {
            jsonRequest = String.format("""
                    {
                      "clientName": "%s",
                      "clientNameEn": "%s",
                      "applicationType": "%s",
                      "redirectUris": %s,
                      "scope": ["adaa"],
                      "encryptionKey": "MnM1djh5L0I/RShIK01iUWVUaFdtWnEzdDZ3OXokQyY=",
                      "encryptionAlg": "AES-256",
                      "softwareStatement": "eyJhbGciOiJIUzI1NiJ9.eyJ2ZW5kb3JOYW1lIjoiQ29tcGFueSBhLnMuIiwic29mdHdhcmVOYW1lIjoiTmVqbGVwxaHDrSBwcm9kdWt0Iiwic29mdHdhcmVOYW1lRW4iOiJCZXN0IHByb2R1Y3QiLCJzb2Z0d2FyZUlkIjoiZjY0YmYyZTQ0N2U1NDUyMjhjNzhlMDdiMDgxYTgyZWUiLCJzb2Z0d2FyZVZlcnNpb24iOiIxLjAiLCJzb2Z0d2FyZVVyaSI6Imh0dHBzOi8vY2xpZW50LmV4YW1wbGUub3JnIiwicmVkaXJlY3RVcmlzIjpbImh0dHBzOi8vY2xpZW50LmV4YW1wbGUub3JnL2NhbGxiYWNrIiwiaHR0cHM6Ly9jbGllbnQuZXhhbXBsZS5vcmcvY2FsbGJhY2stYmFja3VwIl0sInRva2VuRW5kcG9pbnRBdXRoTWV0aG9kIjoiY2xpZW50X3NlY3JldF9wb3N0IiwiZ3JhbnRUeXBlcyI6WyJhdXRob3JpemF0aW9uX2NvZGUiXSwicmVzcG9uc2VUeXBlcyI6WyJjb2RlIl0sInJlZ2lzdHJhdGlvbkJhY2tVcmkiOiJodHRwczovL2NsaWVudC5leGFtcGxlLm9yZy9iYWNrdXJpIiwiY29udGFjdHMiOlsiZW1haWw6IGV4YW1wbGVAZ29vZHNvZnQuY29tIl0sImxvZ29VcmkiOiJodHRwczovL2NsaWVudC5leGFtcGxlLm9yZy9sb2dvLnBuZyIsInRvc1VyaSI6Imh0dHBzOi8vY2xpZW50LmV4YW1wbGUub3JnL3RvcyIsInBvbGljeVVyaSI6Imh0dHBzOi8vY2xpZW50LmV4YW1wbGUub3JnL3BvbGljeSJ9.Fw11ePNty7aimkm3yBYMoK5L-8Blpec4CafNJ-giC4g"
                    }""",
                    props.getProperty("application.name"),
                    props.getProperty("application.name.en"),
                    props.getProperty("application.type"),
                    props.getProperty("redirect.uri")
            );
        }
        jsonByte64 = Arrays.toString(Base64.getEncoder().encode(jsonRequest.getBytes()));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api-gateway.kb.cz/sandbox/client-registration-ui/v2/saml/register?registrationRequesthttps://api-gateway.kb.cz/sandbox/client-registration-ui/v2/saml/register?registrationRequest=" + jsonByte64 + "&state=client123"))
                .build();
        HttpResponse<String> response = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(20)).build().send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 302) {
            URI body = URI.create(response.body());
            String parameters = body.getQuery();
            String salt = null;
            String cipherText = null;
            for (var param : parameters.split("&")) {
                String[] pair = param.split("=");
                if (pair[0].equals("salt")) {
                    salt = pair[1];
                } else if (pair[0].equals("encryptedData")) {
                    cipherText = pair[1];
                }
            }
            return ResponseDecryption.decrypt(cipherText, salt, "key");
        }
        return null;
    }
}
