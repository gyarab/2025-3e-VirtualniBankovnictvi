package cz.gyarabProject.api.adaa;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.gyarabProject.api.datatype.AccessToken;
import cz.gyarabProject.api.datatype.ClientInfo;
import cz.gyarabProject.api.datatype.Code;

import static cz.gyarabProject.api.Helper.*;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Properties;

public class Token {
    private final Properties props;

    public Token(Properties props) {
        this.props = props;
    }

    /**
     * Create a URL like {@link String} from which will be returned the token.
     *
     * @return URL for getting a token.
     */
    public String getCode(String clientId, String redirectUrl) {
        return "https://login.kb.cz/autfe/ssologin?response_type=code&client_id=" + clientId + "&redirect_uri=" +
                redirectUrl +
                "&scope=" + props.getProperty("scopes").replace(props.getProperty("array.separator"), "%20");
    }

    /**
     * Create refresh token and access token. The refresh token will save to file and access token return.
     * @see #getToken
     */
    public AccessToken getRefreshToken(String redirectUrl, Code code, ClientInfo client)
            throws IOException, InterruptedException {
        return getToken(redirectUrl, code.code(), client, "authorization_code");
    }

    public AccessToken getAccessToken(String redirectUrl, String redirectToken, ClientInfo client)
        throws IOException, InterruptedException {
        return getToken(redirectUrl, redirectToken, client, "refresh_token");
    }

    /**
     * Request KBs api for refresh token and from the response save refresh token to file and rest will save to
     * {@link AccessToken}.
     *
     * @param client ID and secret from {@link ClientInfo} for request.
     * @param redirectUrl Redirect URL for request.
     * @param codeOrToken Content string of {@link Code} or refresh token for request.
     * @param grandType Specify type of codeOrToken.
     * @return {@link AccessToken} from response.
     * @throws IOException Problem with sending request to the server, mapping to {@link JsonNode} or
     * {@link AccessToken} or while writing the refresh token to file.
     * @throws InterruptedException Prolem with sending request to the server.
     */
    private AccessToken getToken(String redirectUrl, String codeOrToken, ClientInfo client, String grandType)
            throws IOException, InterruptedException {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create("https://api-gateway.kb.cz/oauth2/v3/access_token"));

        builder.header("x-correlation-id", props.getProperty("header.value.x-correlation-id"));
        builder.header("apiKey", getKey());
        builder.header("Content-Type", "application/x-www-form-urlencoded");

        builder.POST(getRequestBody(redirectUrl, codeOrToken, client, grandType));

        HttpResponse<String> response = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(20))
                .build()
                .send(builder.build(), HttpResponse.BodyHandlers.ofString());

        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(response.body());
        if (node.has("refresh_token")) {
            createAndWriteFile(getAbsolutePath(props, "key-token.path", "token.refresh.path"), node.get("refresh_token").asText());
        }
        return mapper.treeToValue(node, AccessToken.class);
    }

    /**
     * Read and return file with api key.
     *
     * @return Api key.
     * @throws IOException When the file doesn't exist or can't be read.
     */
    private String getKey() throws IOException {
        return readValidFile(getAbsolutePath(props, "key-token.path", "key.path"));
    }

    /**
     * Create the body of request and return it as {@link HttpRequest.BodyPublisher}
     *
     * @param codeOrToken Code or refresh token.
     * @param client Stored data of client (id and secret is needed).
     * @param grandType Specify type of codeOrToken.
     * @return {@link HttpRequest.BodyPublisher}.
     */
    private HttpRequest.BodyPublisher getRequestBody(String redirectUrl, String codeOrToken, ClientInfo client, String grandType) {
        String key;
        if (grandType.equals("authorization_code")) {
            key = "&code=";
        } else {
            key = "&refresh_token=";
        }
        String body = "redirect_uri=" + URLEncoder.encode(redirectUrl)
                + key + URLEncoder.encode(codeOrToken)
                + "&client_id=" + URLEncoder.encode(client.id())
                + "&client_secret=" + URLEncoder.encode(client.secret())
                + "&grant_type=" + URLEncoder.encode(grandType);
        return HttpRequest.BodyPublishers.ofString(body);
    }
}
