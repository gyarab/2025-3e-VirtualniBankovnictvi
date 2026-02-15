package cz.gyarabProject.api.sporitelna.datatype;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.stereotype.Component;

@Component
public class Token {
    private AccessToken access = null;
    private RefreshToken refresh = null;

    public void setAccess(AccessToken token) {
        this.access = token;
    }

    public String getAccess() {
        if (access == null) return null;
        return access.token;
    }

    public void setRefresh(RefreshToken token) {
        this.refresh = token;
    }

    public String getRefresh() {
        if (refresh == null) return null;
        return refresh.token;
    }

    public boolean isValid() {
        if (access == null || refresh == null) {
            return false;
        }
        return access.createdAt + access.expiresIn > System.currentTimeMillis() / 1000 &&
                refresh.createdAt + RefreshToken.expiresIn > System.currentTimeMillis() / 1000;
    }

    public boolean isRefreshValid() {
        if (refresh == null) {
            return false;
        }
        return refresh.createdAt + RefreshToken.expiresIn > System.currentTimeMillis() / 1000 &&
                refresh != null;
    }

    public long accessExpireTime() {
        return access.createdAt + access.expiresIn;
    }

    public long refreshExpireTime() {
        return refresh.createdAt + RefreshToken.expiresIn;
    }


    @JsonIgnoreProperties(ignoreUnknown = true)
    public record AccessToken(
            @JsonProperty(value = "access_token", required = true) String token,
            @JsonProperty(value = "token_type") String type,
            @JsonProperty(value = "expires_in", required = true) int expiresIn,
            @JsonProperty(value = "scope") String scope,
            long createdAt
    ) {

        public AccessToken {
            createdAt = System.currentTimeMillis() / 1000;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record RefreshToken(
            @JsonProperty(value="refresh_token", required = true) String token,
            long createdAt
    ) {
        public static final int expiresIn = 86400;
        public RefreshToken {
            createdAt = System.currentTimeMillis() / 1000;
        }
    }
}
