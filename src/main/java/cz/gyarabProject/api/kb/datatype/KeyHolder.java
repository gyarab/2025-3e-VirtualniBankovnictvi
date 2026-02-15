package cz.gyarabProject.api.kb.datatype;

import cz.gyarabProject.api.Helper;
import org.springframework.stereotype.Component;

//import java.io.Console;
import java.io.IOException;

@Component
public class KeyHolder {
    private final String apiKey;
    private final String clientRegistrationKey;
    private final String oAuth2Key;

    private final String apiKeySandbox;
    private final String clientRegistrationKeySandbox;
    private final String contactRequestKeySandbox;
    private final String oAuth2KeySandbox;

    private Deployment deployment;

//    public KeyHolder() {
//        Console console = System.console();
//        if (console == null) {
//            System.out.println("No console available");
//            throw new RuntimeException("No console available.");
//        }
//
//        apiKey = new String(console.readPassword("write a key for api of this project: "));
//    }

    public KeyHolder() throws IOException {
        apiKey = Helper.readValidFile("keys-tokens/api-key.txt");
        clientRegistrationKey = Helper.readValidFile("keys-tokens/client-registration-key.txt");
        oAuth2Key = Helper.readValidFile("keys-tokens/oauth2-key.txt");

        apiKeySandbox = Helper.readValidFile("keys-tokens/api-key-sandbox.txt");
        clientRegistrationKeySandbox = Helper.readValidFile("keys-tokens/client-registration-key-sandbox.txt");
        contactRequestKeySandbox = Helper.readValidFile("keys-tokens/contact-request-key-sandbox.txt");
        oAuth2KeySandbox = Helper.readValidFile("keys-tokens/oauth2-key-sandbox.txt");

        deployment = Deployment.PRODUCTION;
    }

    public String getApi() {
        return deployment == Deployment.SANDBOX ? apiKeySandbox : apiKey;
    }

    public String getClientRegistrationKey() {
        return deployment == Deployment.SANDBOX ? clientRegistrationKeySandbox : clientRegistrationKey;
    }

    public String getOAuth2Key() {
        return deployment == Deployment.SANDBOX ? clientRegistrationKeySandbox : oAuth2Key;
    }

    public String getContactRequestKeySandbox() {
        return contactRequestKeySandbox;
    }

    public void changeDeployment(Deployment deployment) {
        this.deployment = deployment;
    }

    public void switchToSandbox() {
        deployment = Deployment.SANDBOX;
    }

    public void switchToProduction() {
        deployment = Deployment.PRODUCTION;
    }

    public Deployment getDeployment() {
        return deployment;
    }

    @Override
    public String toString() {
        return "";
    }
}
