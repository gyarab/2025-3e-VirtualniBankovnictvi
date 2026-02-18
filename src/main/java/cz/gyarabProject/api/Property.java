package cz.gyarabProject.api;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Map;
import java.util.Properties;

@Component
public class Property {
    public enum Bank { CS, KB }
    public enum Environment { SANDBOX, PRODUCTION }
    private final static String propertiesPath = "./src/main/resources/";
    private final Properties props;
    private final static String[] files = {
            "custom.properties",
            "cs.properties",
            "cs.secret.properties" /*,
            "kb.properties"*/ };

    public Property() {
        props = new Properties();
        for (String file : files) {
            try (InputStream in = getClass().getClassLoader().getResourceAsStream(file)) {
                props.load(in);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public Properties getProps() {
        return props;
    }

    public String get(String key) {
        return props.getProperty(key, "");
    }

    public String get(String key, String defaultValue) {
        return props.getProperty(key, defaultValue);
    }

    private static String getUri(Properties props, String endpoint, Environment enviroment) {
        var builder = new StringBuilder();
        if (props.containsKey("uri")) {
            builder.append(props.getProperty("uri"));
        } else if (props.containsKey("uri." + enviroment.name().toLowerCase())) {
            builder.append(props.containsKey("uri." + enviroment.name().toLowerCase()));
        } else {
            throw new RuntimeException("No uri in property");
        }
        String value = props.getProperty(endpoint);
        if (value == null) {
            value = props.getProperty(endpoint + "." + enviroment.name().toLowerCase());
        }
        if (value == null) {
            throw new RuntimeException("Key " + endpoint + " is not in properties.");
        }
        if (value.contains("https://")) { return value; }
        return builder.append(value).toString();
    }

    public URI getUri(Bank bank, Environment environment, String endpoint, String query, String... uriEnding) {
        StringBuilder ending = new StringBuilder();
        for (var s : uriEnding) {
            ending.append("/").append(s);
        }
        return switch (bank) {
            case KB -> URI.create(getUri(props, endpoint, environment) + ending + query);
            case CS -> URI.create(getUri(props, endpoint, environment) + ending + query);
            default -> throw new RuntimeException(bank.name() + " is not implemented.");
        };
    }

    public URI getUriWithEnding(Bank bank, Environment environment, String endpoint, String... uriEnding) {
        return getUri(bank, environment, endpoint, "", uriEnding);
    }

    public URI getUri(Bank bank, Environment environment, String endpoint, String query) {
        return getUri(bank, environment, endpoint, query, "");
    }

    public URI getUri(Bank bank, Environment environment, String endpoint) {
        return getUri(bank, environment, endpoint, "", "");
    }

    public String buildQuery(Map<String, Object> map) {
        StringBuilder query = new StringBuilder("?");
        map.forEach((k, v) -> query.append(k).append("=").append(v.toString()).append("&"));
        if (!query.isEmpty()) {
            query.deleteCharAt(query.length() - 1);
        }
        return query.toString();
    }
}
