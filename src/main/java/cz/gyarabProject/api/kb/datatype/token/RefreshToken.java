package cz.gyarabProject.api.kb.datatype.token;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RefreshToken(@JsonProperty(value = "refresh_token") String refreshToken,
                           AccessToken accessToken,
                           long createdAt,
                           @JsonProperty(value = "expires_in") int expiresIn) {

    /**
     * Set the {@code expiresIn} value on half of year if the setted time is not in interval day to half of year.
     *
     * @param refreshToken refresh token.
     * @param accessToken access token.
     * @param expiresIn time in which will the {@code refreshToken} expire.
     */
    public RefreshToken {
        createdAt = System.currentTimeMillis() / 1000;
        if (86_400 >= expiresIn || expiresIn >= 15_778_463) {
            expiresIn = 15_778_463;
        }
    }

    public boolean isValid() {
        return createdAt + expiresIn > System.currentTimeMillis() / 1000;
    }

    public RefreshToken setAccessToken(AccessToken accessToken) {
        return new RefreshToken(this.refreshToken, accessToken, createdAt, expiresIn);
    }
}
