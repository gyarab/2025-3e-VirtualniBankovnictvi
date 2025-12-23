package cz.gyarabProject.__3e_VirtualniBankovnictvi;

import cz.gyarabProject.api.adaa.JWT;

import cz.gyarabProject.api.adaa.Registration;
import cz.gyarabProject.api.adaa.Token;
import cz.gyarabProject.api.datatype.AccessToken;
import cz.gyarabProject.api.datatype.ClientInfo;
import cz.gyarabProject.api.datatype.Code;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Properties;

import jakarta.servlet.http.HttpServletResponse;

import static cz.gyarabProject.api.Helper.getAbsolutePath;
import static cz.gyarabProject.api.Helper.readValidFile;

@RestController
public class TestOfApi {
    private final String propertiesPath = "./src/main/resources/";

    Properties props = new Properties();
    final boolean SANDBOX = true;
    final String encryptionKey = "MnM1djh5L0I/RShIK01iUWVUaFdtWnEzdDZ3OXokQyY=";
    ClientInfo clientInfo = null;
    Code code = null;

    public TestOfApi() throws IOException {
        props.load(new FileInputStream(propertiesPath + "api.properties"));
        props.load(new FileInputStream(propertiesPath + "custom.properties"));
    }

    /**
     * Test the {@link JWT} class by trying to generate new token and reading it as result.
     *
     * @return last generated token.
     * @throws IOException when file with token cannot be read or body of POST request is invalid JSON.
     * @throws InterruptedException when sending request is interrupted.
     */
    @GetMapping(value="/test-of-api/newJWT")
    public String newJWT() throws IOException, InterruptedException {
        System.out.println(System.getProperty("user.dir") + " test");
        try {
            new JWT(props).createNewTlsAuthentication();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return Files.readString(Paths.get("./keys-tokens/" + props.getProperty("jwt.path")));
    }

    /**
     * Test the {@link Registration} class redirects and response decryption.
     *
     * @param response variable from {@code Spring} for sending followed redirected requests.
     * @param salt returned {@link Base64} encoded {@link String} from bank for decryption.
     * @param encryptedData returned {@link Base64} encoded {@link String} response from bank.
     * @return decrypted JSON like {@link String} from bank
     */
    @GetMapping(value="/test-of-api/registration")
    public String registration(HttpServletResponse response,
                               @RequestParam(value="salt", required=false, defaultValue="") String salt,
                               @RequestParam(value="encryptedData", required=false, defaultValue="") String encryptedData
    ) {
        Registration register = new Registration(props);
        try {
            if (salt.isEmpty() || encryptedData.isEmpty()) {
                String url;
                if (SANDBOX) {
                    url = register.getRegistration(encryptionKey);
                } else {
                    url = register.getRegistration("");
                }

                response.sendRedirect(url);
            } else {
                String key;
                if (SANDBOX) {
                    key = encryptionKey;
                }
                clientInfo = register.decryptResponse(encryptedData, salt, key);
                return clientInfo.toString();
            }
        } catch (IOException e) {
            return e.getMessage();
        }
        return "";
    }

    @GetMapping(value="test-of-api/token", produces= MediaType.TEXT_HTML_VALUE)
    public String token(HttpServletResponse response,
                        @RequestParam(value="code", required=false, defaultValue="") String code,
                        @RequestParam(value="state", required=false, defaultValue="") String state
    ) {
        if (clientInfo == null) {
            return "You have to register here: <a href='http://localhost:8080/test-of-api/registration>Register</a>'";
        }
        String redirectUrl = "http://localhost:8080/test-of-api/token";
        Token token = new Token(props);
        try {
            if (code.isEmpty()) {
                response.sendRedirect(token.getCode(clientInfo.id(), redirectUrl));
            } else if (this.code == null) {
                this.code = new Code(code, System.currentTimeMillis() / 1000);
                AccessToken accessToken = token.getRefreshToken(redirectUrl, this.code, clientInfo);
                return accessToken.toString();
            } else {
                AccessToken accessToken = token.getAccessToken(redirectUrl,
                        readValidFile(getAbsolutePath(props, "key-token.path", "token.refresh.path")),
                        clientInfo);
                return accessToken.toString();
            }
        } catch (IOException | InterruptedException e) {
            return e.getMessage();
        }
        return "";
    }
}
