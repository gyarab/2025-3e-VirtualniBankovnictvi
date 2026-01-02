package cz.gyarabProject.__3e_VirtualniBankovnictvi;

import cz.gyarabProject.api.adaa.*;

import cz.gyarabProject.api.datatype.*;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Base64;
import java.util.Random;

import jakarta.servlet.http.HttpServletResponse;


@RestController
public class TestOfApi {
    private final static String propertiesPath = "./src/main/resources/";

    private final AccountRequest accountRequest;
    private final BalanceRequest balanceRequest;
    private final JwtRequest jwtRequest;
    private final RegistrationRequest registrationRequest;
    private final TokenRequest tokenRequest;

    final boolean SANDBOX = false;
    String encryptionKey;
    BankClientInfo clientInfo = null;
    Code code = null;
    String jwt = null;
    RefreshToken refreshToken = null;

    public TestOfApi(
            AccountRequest accountRequest,
            BalanceRequest balanceRequest,
            JwtRequest jwtRequest,
            RegistrationRequest registrationRequest,
            TokenRequest tokenRequest
    ) {
        this.accountRequest = accountRequest;
        this.balanceRequest = balanceRequest;
        this.jwtRequest = jwtRequest;
        this.registrationRequest = registrationRequest;
        this.tokenRequest = tokenRequest;
    }

    /**
     * Test the {@link JwtRequest} class by trying to generate new token and reading it as result.
     *
     * @return last generated token.
     * @throws InterruptedException when sending request is interrupted.
     */
    @GetMapping(value = "/test-of-api/newJWT")
    public String newJWT() throws InterruptedException {
        try {
            jwt = jwtRequest.getNewJwt();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return jwt;
    }

    /**
     * Test the {@link RegistrationRequest} class redirects and response decryption.
     *
     * @param response variable from {@code Spring} for sending followed redirected requests.
     * @param salt returned {@link Base64} encoded {@link String} from bank for decryption.
     * @param encryptedData returned {@link Base64} encoded {@link String} response from bank.
     * @return decrypted JSON like {@link String} from bank
     */
    @GetMapping(value = "/test-of-api/registration")
    public String registration(HttpServletResponse response,
                               @RequestParam(value = "salt", required = false, defaultValue = "") String salt,
                               @RequestParam(value = "encryptedData", required = false, defaultValue = "") String encryptedData
    ) {
        try {
            if (salt.isEmpty() || encryptedData.isEmpty()) {
                if (SANDBOX) {
                    encryptionKey = "MnM1djh5L0I/RShIK01iUWVUaFdtWnEzdDZ3OXokQyY=";
                } else {
                    encryptionKey = generateEncryprionKey();
                }
                String url;
                url = registrationRequest.getRegistration(encryptionKey, jwt);
                response.sendRedirect(url);
            } else {
                String key;
                clientInfo = registrationRequest.decryptResponse(encryptedData, salt, encryptionKey);
                return clientInfo.toString();
            }
        } catch (IOException e) {
            return e.getMessage();
        }
        return "";
    }

    private String generateEncryprionKey() {
        byte[] bytes = new byte[32];
        new Random().nextBytes(bytes);
        return Base64.getEncoder().encodeToString(bytes);
    }

    @GetMapping(value = "test-of-api/token", produces = MediaType.TEXT_HTML_VALUE)
    public String token(HttpServletResponse response,
                        @RequestParam(value = "code", required = false, defaultValue = "") String code,
                        @RequestParam(value = "state", required = false, defaultValue = "") String state
    ) {
        if (clientInfo == null) {
            return "You have to register here: <a href='http://localhost:8080/test-of-api/registration>Register</a>'";
        }
        String redirectUrl = "http://localhost:8080/test-of-api/token";
        try {
            if (code.isEmpty()) {
                response.sendRedirect(tokenRequest.getCode(clientInfo.id(), redirectUrl));
            } else if (this.code == null) {
                this.code = new Code(code, System.currentTimeMillis() / 1000);
                refreshToken = tokenRequest.getRefreshToken(redirectUrl, this.code, clientInfo);
                return refreshToken.toString();
            } else {
                refreshToken = tokenRequest.getAccessToken(redirectUrl,
                        clientInfo,
                        refreshToken);
                return refreshToken.accessToken().toString();
            }
        } catch (IOException | InterruptedException e) {
            return e.getMessage();
        }
        return "";
    }
}
