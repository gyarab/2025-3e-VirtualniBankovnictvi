package cz.gyarabProject.api.adaa;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.gyarabProject.api.datatype.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Component
public class TokenRequest {
    private final Property props;
    private final KeyHolder keyHolder;

    public TokenRequest(Property props, KeyHolder keyHolder) {
        this.props = props;
        this.keyHolder = keyHolder;
    }

    /**
     * Create a URL like {@link String} from which will be returned the token.
     *
     * @return URL for getting a token.
     */
    public String getCode(String clientId, String redirectUrl) {
        return props.getAbsolutePath("kb.login.uri") + "?response_type=code&client_id=" + clientId + "&redirect_uri=" +
                redirectUrl +
                "&scope=" + props.get("scopes").replace(props.get("array.separator"), "%20");
    }

    /**
     * Create refresh token and access token. The refresh token will save to file and access token return.
     * @see #getToken
     */
    public RefreshToken getRefreshToken(String redirectUrl, Code code, ClientInfo client)
            throws IOException, InterruptedException {
        return getToken(redirectUrl, code.code(), client, "authorization_code", null);
    }

    public RefreshToken getAccessToken(String redirectUrl, ClientInfo client, RefreshToken token)
        throws IOException, InterruptedException {
        return getToken(redirectUrl, token.refreshToken(), client, "refresh_token", token);
    }

    /**
     * Request KBs api for refresh token and from the response save refresh token to file and rest will save to
     * {@link AccessToken}.
     *
     * @param client ID and secret from {@link ClientInfo} for request.
     * @param redirectUrl Redirect URL for request.
     * @param codeOrToken Content string of {@link Code} or refresh token for request.
     * @param grandType Specify type of codeOrToken.
     * @return {@link RefreshToken} Given token with new {@link AccessToken} from response and if then new {@code RefreshToken.refreshToken} as well.
     * @throws IOException Problem with sending request to the server, mapping to {@link JsonNode} or
     * {@link AccessToken} or while writing the refresh token to file.
     * @throws InterruptedException Prolem with sending request to the server.
     */
    private RefreshToken getToken(String redirectUrl, String codeOrToken, ClientInfo client, String grandType, RefreshToken token)
            throws IOException, InterruptedException {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(props.getAbsolutePath("kb.uri", "kb.uri.token")));

        builder.header("x-correlation-id", props.get("header.value.x-correlation-id"));
        builder.header("apiKey", keyHolder.getApi());
        builder.header("Content-Type", "application/x-www-form-urlencoded");

        builder.POST(getRequestBody(redirectUrl, codeOrToken, client, grandType));

        HttpResponse<String> response = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(20))
                .build()
                .send(builder.build(), HttpResponse.BodyHandlers.ofString());

        ObjectMapper mapper = new ObjectMapper();
        if (token == null) {
            token = mapper.readValue(response.body(), RefreshToken.class);
        }
        return token.setAccessToken(mapper.readValue(response.body(), AccessToken.class));
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
