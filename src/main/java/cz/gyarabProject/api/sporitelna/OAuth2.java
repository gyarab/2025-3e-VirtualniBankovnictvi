package cz.gyarabProject.api.sporitelna;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.gyarabProject.api.ObjectMappers;
import cz.gyarabProject.api.Property;
import cz.gyarabProject.api.sporitelna.datatype.Token;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@RestController
public class OAuth2 {
    private final Token token;
    private final Property props;
    private final HttpClient client;
    private final ObjectMapper mapper;

    public OAuth2(Token token, Property props, HttpClient client, ObjectMappers mappers) {
        this.token = token;
        this.props = props;
        this.client = client;
        this.mapper = mappers.getMapper();
    }

    public String authUri() {
        String uri = props.get("oauth2.uri.sandbox") + "/auth?redirect_uri=" + (props.get("uri.redirect")) +
                "&client_id=" + props.get("client.id") + "&response_type=" + "code" + "&state=" + "RANDOM" + "&access_type=" + "offline";
        return uri;
    }

    @GetMapping(value="sporitelna_oauth_redirect")
    public void oauthRedirect(
            HttpServletResponse servletResponse,
            @RequestParam(value="code") String code,
            @RequestParam(value="state") String state) throws IOException, InterruptedException {
        HttpRequest.Builder builder = getBuilder("code", code);
        HttpResponse<String> response = client.send(builder.build(), HttpResponse.BodyHandlers.ofString());

        System.out.println(builder.build().toString());
        System.out.println(response.body());

        token.setAccess(mapper.readValue(response.body(), Token.AccessToken.class));
        token.setRefresh(mapper.readValue(response.body(), Token.RefreshToken.class));
        servletResponse.sendRedirect("http://localhost:8080/sporitelna");
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
        HttpRequest.Builder builder = HttpRequest.newBuilder().uri(URI.create(props.get("oauth2.uri.sandbox") + "/token"));
        builder.header("Content-Type", "application/x-www-form-urlencoded");
        builder.POST(getBody(name, code));
        return builder;
    }

    private HttpRequest.BodyPublisher getBody(String name, String code) {
        String body = name + "=" + code + "&client_id=" + props.get("client.id") + "&client_secret=" +
                props.get("client.secret") + "&grant_type=" +
                (name.equals("code") ? "authorization_code&redirect_uri=" + props.get("uri.redirect"): name);

        System.out.println(body);
        return HttpRequest.BodyPublishers.ofString(body);
    }

    public void logout() throws IOException, InterruptedException {
        HttpRequest.Builder builder = HttpRequest.newBuilder().uri(URI.create(props.get("oauth2.uri.sandbox") + "/revokeext"));
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
