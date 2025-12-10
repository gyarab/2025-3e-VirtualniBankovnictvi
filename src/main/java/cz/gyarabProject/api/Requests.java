package cz.gyarabProject.api;

import Decryption.ResponseDecryption;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.time.Duration;
import java.util.Arrays;
import java.util.Base64;
import java.util.Properties;
import java.util.stream.Collectors;

import org.json.JSONObject;

public class Requests {
    private final String keysTokensPath = "./keys-tokens/";
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

    private boolean isValidJson(String json) {
        try {
             new JSONObject(json);
             return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    private String[] cutToTwo(String[] string) {
        if (string.length > 2) {
            return new String[] { string[0], string[1] };
        }
        return string;
    }

    private String fromStringToArray(String string) {
        return Arrays.stream(cutToTwo(string.split(separator))).map(s -> "\"email: " + s + "\"")
                .collect(Collectors.joining(",", "[", "]"));
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
        request.header("Content-Type", props.getProperty("header.value.content-type"));

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
                fromStringToArray(props.getProperty("redirect.uri")),
                props.getProperty("registration.back.uri"),
                Arrays.stream(cutToTwo(props.getProperty("contacts").split(separator))).map(s -> "\"email: " + s + "\"")
                        .collect(Collectors.joining(",", "[", "]")),
                props.getProperty("logo.uri"),
                props.getProperty("tos.uri"),
                props.getProperty("policy.uri")
        );
        System.out.println("Body: \n" + body);

        if (!isValidJson(body)) {
            throw new IOException("body is invalid JSON");
        }

        request.POST(HttpRequest.BodyPublishers.ofString(body));
        HttpResponse<String> response = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(20))
                .build()
                .send(request.build(), HttpResponse.BodyHandlers.ofString());

        createAndWriteFile(keysTokensPath + props.getProperty("jwt.path"), response.body());
    }

    public String getRegistration() throws IOException {
        String jsonRequest;
        String jsonByte64;
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
                  "softwareStatement": "%s",
                  "encryptionAlg": "AES-256",
                  "encryptionKey": "MnM1djh5L0I/RShIK01iUWVUaFdtWnEzdDZ3OXokQyY="
                }""",
                props.getProperty("application.name"),
                props.getProperty("application.name.en"),
                props.getProperty("application.type"),
                fromStringToArray(props.getProperty("redirect.uri")),
                fromStringToArray(props.getProperty("api.scopes")),
                jwt
        );
        jsonByte64 = new String(Base64.getEncoder().encode(jsonRequest.getBytes()));

        System.out.println("JSON Request: \n" + jsonByte64 + "\n");

        return "https://api-gateway.kb.cz/sandbox/client-registration-ui/v2/saml/register?registrationRequest="
                + jsonByte64 + "&state=client123";
    }

    public String fetchResponse(HttpResponse<String> response) {
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

    public String fetchResponse(String response) {
        URI body = URI.create(response);
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
}
