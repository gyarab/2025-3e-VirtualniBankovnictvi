package cz.gyarabProject.api.adaa;

import Decryption.ResponseDecryption;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.gyarabProject.api.datatype.BankClientInfo;
import cz.gyarabProject.api.datatype.ObjectMappers;
import cz.gyarabProject.api.datatype.Property;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Base64;

import static cz.gyarabProject.api.Helper.*;

@Component
public class RegistrationRequest {
    private final Property props;
    private final String separator;
    private final ObjectMapper mapper;

    public RegistrationRequest(Property props,
                               ObjectMappers mappers) {
        this.props = props;
        this.separator = props.get("array.separator", ", ");
        this.mapper = mappers.getMapper();
    }

    /**
     * Create and return URL like {@link String} for kb with filled {@code registarionRequest} parameter for OAuth2 registration.
     *
     * @return URL as {@link String}.
     * @throws IOException when {@code registrationRequest} JSON is invalid.
     */
    public String getRegistration(String encryptionKey, String jwt) throws IOException {
        String jsonByte64 = new String(Base64.getEncoder().encode(getRequestJson(jwt, encryptionKey).getBytes()));

        return props.getKbUri("oauth2") + "?registrationRequest="
                + jsonByte64 + "&state=client123";
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
                fromStringToArray(props.get("redirect.uri"), separator),
                fromStringToArray(props.get("scopes"), separator),
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
            key = readValidFile(props.getAbsolutePath("key-token.path", "api-key.path"));
            key = new String(encodeBase64(key));
        }
        return mapper.readValue(ResponseDecryption.decrypt(cipherText, iv, key), BankClientInfo.class);
    }
}
