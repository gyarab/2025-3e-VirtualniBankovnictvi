package cz.gyarabProject.api.adaa;

import Decryption.ResponseDecryption;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.gyarabProject.api.datatype.ClientInfo;

import java.io.IOException;
import java.util.Base64;
import java.util.Properties;

import static cz.gyarabProject.api.Helper.*;

public class Registration {
    private final Properties props;
    private final String separator;

    public Registration(Properties props) {
        this.props = props;
        this.separator = props.getProperty("array.separator", ", ");
    }

    /**
     * Create and return URL like {@link String} for kb with filled {@code registarionRequest} parameter for OAuth2 registration.
     *
     * @return URL as {@link String}.
     * @throws IOException when {@code registrationRequest} JSON is invalid.
     */
    public String getRegistration(String encryptionKey) throws IOException {
        String jwt = readValidFile(getAbsolutePath(props, "key-token.path", "jwt.path"));

        String jsonByte64 = new String(Base64.getEncoder().encode(getRequestJson(jwt, encryptionKey).getBytes()));

        return "https://api-gateway.kb.cz/sandbox/client-registration-ui/v2/saml/register?registrationRequest="
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
                props.getProperty("application.name"),
                props.getProperty("application.name.en"),
                props.getProperty("application.type"),
                fromStringToArray(props.getProperty("redirect.uri"), separator),
                fromStringToArray(props.getProperty("scopes"), separator),
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
    public ClientInfo decryptResponse(String cipherText, String iv, String key) throws IOException {
        if (isNullOrEmpty(key)) {
            key = readValidFile(getAbsolutePath(props, "key-token.path", "api-key.path"));
            key = new String(encodeBase64(key));
        }
        return new ObjectMapper().readValue(ResponseDecryption.decrypt(cipherText, iv, key), ClientInfo.class);
    }
}
