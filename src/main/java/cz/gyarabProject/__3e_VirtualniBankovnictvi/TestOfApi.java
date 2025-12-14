package cz.gyarabProject.__3e_VirtualniBankovnictvi;

import cz.gyarabProject.api.JWT;

import cz.gyarabProject.api.Registration;
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

@RestController
public class TestOfApi {
    private final String propertiesPath = "./src/main/resources/";

    Properties props = new Properties();
    final boolean SANDBOX = true;
    final String encryptionKey = "MnM1djh5L0I/RShIK01iUWVUaFdtWnEzdDZ3OXokQyY=";

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
                               @RequestParam(value="encryptedData", required=false, defaultValue="") String encryptedData )
    {
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
                return register.decryptResponse(encryptedData, salt, key);
            }
        } catch (IOException e) {
            return e.getMessage();
        }
        return "";
    }
}
