package cz.gyarabProject.api.datatype;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ClientInfo (
        @JsonProperty("application_type") String appType,
        @JsonProperty("redirect_uris") String[] redirectUri,
        @JsonProperty("client_name") String name,
        @JsonProperty("client_name#en-US") String nameEn,
        @JsonProperty("logo_uri") String logoUri,
        @JsonProperty("policy_uri") String policyUri,
        @JsonProperty("tos_uri") String tosUri,
        @JsonProperty("contacts") String[] contacts,
        @JsonProperty("scopes") String[] scopes,
        @JsonProperty("response_types") String[] responseTypes,
        @JsonProperty("grant_types") String[] grantTypes,
        @JsonProperty("subject_type") String subjectType,
        @JsonProperty("token_endpoint_auth_method") String tokenEndpoint,
        @JsonProperty("require_auth_time") boolean requireAuthTime,
        @JsonProperty("bin") String bin,
        @JsonProperty("client_id") String id,
        @JsonProperty("client_secret") String secret,
        @JsonProperty("state") String state,
        @JsonProperty("api_key") String apiKey,
        @JsonProperty("registration_client_uri") String registrationUri,
        @JsonProperty("client_id_issued_at") int idIssuedAt
) {
    @Override
    public String toString() {
        try {
            return new ObjectMapper().writeValueAsString(this).replace(",", ",<br>");
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
