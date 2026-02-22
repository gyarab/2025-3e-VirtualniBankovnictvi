package cz.gyarabProject.api.kb.datatype.token;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AccessToken(@JsonProperty(value="access_token") String token,
                          @JsonProperty(value="scope") String scope,
                          @JsonProperty(value="token_type") String type,
                          long createdAt,
                          @JsonProperty(value="expires_in") int expiresIn) {

    public AccessToken {
        createdAt = System.currentTimeMillis() / 1000;
        if (expiresIn > 600) {
            expiresIn = 179;
        }
    }

    public String getTypeToken() {
        return type + " " + token;
    }

    public boolean isValid() {
        return createdAt + expiresIn > System.currentTimeMillis() / 1000;
    }
}
