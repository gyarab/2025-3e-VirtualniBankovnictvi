package cz.gyarabProject.api.kb.adaa;

import Decryption.ResponseDecryption;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.gyarabProject.api.kb.datatype.BankClientInfo;
import cz.gyarabProject.api.ObjectMappers;
import cz.gyarabProject.api.Property;
import cz.gyarabProject.api.kb.datatype.KeyHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Base64;
import java.util.Map;

import static cz.gyarabProject.api.Helper.*;

@Component
public class RegistrationRequest {
    private final Property props;
    private final String separator;
    private final ObjectMapper mapper;
    private final KeyHolder keyHolder;
    private static final Property.Bank bank = Property.Bank.KB;

    public RegistrationRequest(Property props,
                               ObjectMappers mappers,
                               KeyHolder keyHolder) {
        this.props = props;
        this.separator = props.get(bank, "array.separator", ", ");
        this.mapper = mappers.getMapper();
        this.keyHolder = keyHolder;
    }

    /**
     * Create and return URL like {@link String} for kb with filled {@code registarionRequest} parameter for OAuth2 registration.
     *
     * @return URL as {@link String}.
     * @throws IOException when {@code registrationRequest} JSON is invalid.
     */
    public String getRegistration(String encryptionKey, String jwt) throws IOException {
        String jsonByte64 = new String(Base64.getEncoder().encode(getRequestJson(jwt, encryptionKey).getBytes()));

        String query = props.buildQuery(Map.of("registrationRequest", jsonByte64, "state", "client123"));
        return props.getUri(bank, Property.Environment.SANDBOX, "oauth2", query).toString();
    }

    /**
     * Generate a JSON like {@link String} for {@code getRegistration}.
     *
     * @param jwt JWT as parameter for request.
     * @param encryptionKey key as parameter for request.
     * @return JSON like {@link String}.
     * @throws IOException when generated {@link String} is invalid JSON.
     */
    private String getRequestJson(String jwt, String encryptionKey) throws IOException {
        String jsonRequest = String.format("""
                {
                  "clientName": "%s",
                  "clientNameEn": "%s",
                  "applicationType": "%s",
                  "redirectUris": %s,
                  "scope": %s,
                  "softwareStatement": "%s",
                  "encryptionAlg": "AES-256",
                  "encryptionKey": "%s"
                }""",
                props.get("application.name"),
                props.get("application.name.en"),
                props.get("application.type"),
                fromStringToArray(props.get(bank, "redirect.uri"), separator),
                fromStringToArray(props.get(bank, "scopes"), separator),
                jwt,
                encryptionKey
        );
        if (!isValidJson(jsonRequest)) {
            throw new IOException("Invalid json request. JSON: " + jsonRequest);
        }

        return jsonRequest;
    }

    /**
     * Decrypt the response returned from URL geathered in {@code getRegistration}.
     *
     * @param cipherText crypted text.
     * @param iv iv (or salt) for {@code cipherText}.
     * @param key key by which is {@code cipherText} crypted.
     * @return JSON like {@link String} which is crypted in {@code cipherText}.
     * @throws IOException when {@code cipherText} is invalid JSON or cannot be decrypted by iv and key.
     */
    public BankClientInfo decryptResponse(String cipherText, String iv, String key) throws IOException {
        if (isNullOrEmpty(key)) {
            key = keyHolder.getApi();
            key = new String(encodeBase64(key));
        }
        return mapper.readValue(ResponseDecryption.decrypt(cipherText, iv, key), BankClientInfo.class);
    }
}
