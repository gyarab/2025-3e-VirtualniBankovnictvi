package cz.gyarabProject.api.datatype;

import org.springframework.stereotype.Component;

import java.io.Console;

@Component
public class KeyHolder {
    private final String apiKey;

    public KeyHolder() {
        Console console = System.console();
        if (console == null) {
            throw new RuntimeException("No console available.");
        }

        apiKey = new String(console.readPassword("write a key for api of this project: "));
    }

    public String getApi() {
        return apiKey;
    }

    @Override
    public String toString() {
        return "";
    }
}
