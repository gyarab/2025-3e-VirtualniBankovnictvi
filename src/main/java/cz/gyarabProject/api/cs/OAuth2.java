package cz.gyarabProject.api.cs;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.gyarabProject.api.ObjectMappers;
import cz.gyarabProject.api.Property;
import cz.gyarabProject.api.cs.datatype.Token;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

@RestController
public class OAuth2 {
    private final Token token;
    private final Property props;
    private final HttpClient client;
    private final ObjectMapper mapper;
    private static final Property.Bank bank = Property.Bank.CS;

    public OAuth2(Token token, Property props, HttpClient client, ObjectMappers mappers) {
        this.token = token;
        this.props = props;
        this.client = client;
        this.mapper = mappers.getMapper();
    }

    public String authUri(String state) {
        if (state == null) {
            state = "";
        }
        String query = props.buildQuery(Map.of(
                "redirect_uri", props.get(bank, "uri.redirect"),
                "client_id", props.get(bank, "client.id"),
                "response_type", "code",
                "state", state,
                "access_type", "offline")
        );
        String uri = props.getUri(bank, Property.Environment.SANDBOX, "oauth2", query, "auth").toString();
        return uri;
    }

    public String authUri() {
        return authUri("");
    }

    @GetMapping("/sporitelna_oauth_redirect")
    public void oauthRedirect(
            HttpServletResponse servletResponse,
            @RequestParam(value="code") String code,
            @RequestParam(value="state") String state
    ) throws IOException, InterruptedException {
        HttpRequest.Builder builder = getBuilder("code", code);
        HttpResponse<String> response = client.send(builder.build(), HttpResponse.BodyHandlers.ofString());

        token.setAccess(mapper.readValue(response.body(), Token.AccessToken.class));
        token.setRefresh(mapper.readValue(response.body(), Token.RefreshToken.class));
        if (state == null || state.isEmpty()) {
            servletResponse.sendRedirect("http://localhost:8080/sporitelna");
            return;
        }
        servletResponse.sendRedirect(state);
    }

    public void newAccessToken() throws IOException, InterruptedException {
        if (!token.isRefreshValid()) {
            token.setRefresh(null);
            return;
        }
        HttpRequest.Builder builder = getBuilder("refresh_token", token.getRefresh());
        HttpResponse<String> response = client.send(builder.build(), HttpResponse.BodyHandlers.ofString());

        token.setAccess(mapper.readValue(response.body(), Token.AccessToken.class));
    }

    private HttpRequest.Builder getBuilder(String name, String code) {
        HttpRequest.Builder builder = HttpRequest.newBuilder().uri(
                props.getUriWithEnding(bank, Property.Environment.SANDBOX, "oauth2", "token"));
        builder.header("Content-Type", "application/x-www-form-urlencoded");
        builder.POST(HttpRequest.BodyPublishers.ofString(getBody(name, code)));
        return builder;
    }

    private String getBody(String name, String code) {
        String body = props.buildQuery(Map.of(name, code,
                "client_id", props.get(bank, "client.id"),
                "client_secret", props.get(bank, "client.secret"),
                "grant_type", (name.equals("code") ?
                        "authorization_code&redirect_uri=" + props.get(bank, "uri.redirect") : name)
        ));
        return body.replaceFirst("\\?", "");
    }

    public void logout() throws IOException, InterruptedException {
        HttpRequest.Builder builder = HttpRequest.newBuilder().uri(
                props.getUriWithEnding(bank, Property.Environment.SANDBOX, "oauth2", "/revokeext")
        );
        builder.header("Content-Type", "application/x-www-form-urlencoded");
        if (!token.isRefreshValid()) {
            return;
        }
        builder.POST(HttpRequest.BodyPublishers.ofString("token=" + token.getRefresh()));

        if (client.send(builder.build(), HttpResponse.BodyHandlers.ofString()).statusCode() != 200) {
            throw new IllegalStateException("Invalid token in log out.");
        }
        token.setRefresh(null);
        token.setAccess(null);
    }
}
