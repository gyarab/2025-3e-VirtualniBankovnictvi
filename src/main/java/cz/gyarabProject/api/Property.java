package cz.gyarabProject.api;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;

@Component
public class Property {
    public enum Bank { CS, KB }
    public enum Environment { SANDBOX, PRODUCTION }
    private final static String propertiesPath = "./src/main/resources/";
    private final Dictionary<String, Properties> props;
    private final static String customPropsKey = "API";
    private final static String[][] files = {
            {
                "custom.properties"
            },
            {
                "cs.properties", "cs.secret.properties"
            },
            {
                "kb.properties"
            }
    };

    public Property() {
        props = new Hashtable<>();
        for (int i = 0; i < files.length; i++) {
            String key;
            if (i == 0) {
                key = customPropsKey;
                props.put(key, new Properties());
            } else {
                key = Bank.values()[i - 1].name();
                props.put(key, new Properties());
            }
            for (String file : files[i]) {
                try (InputStream in = getClass().getClassLoader().getResourceAsStream(file)) {
                    props.get(key).load(in);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private Properties getProps(String prop) {
        return props.get(prop);
    }

    public Properties getProps() {
        return getProps(customPropsKey);
    }

    public Properties getProps(Bank prop) {
        return getProps(prop.name());
    }

    private String get(String prop, String key, String defaultValue) {
        return props.get(key).getProperty(prop, defaultValue);
    }

    public String get(String key) {
        return get(customPropsKey, key, "");
    }

    public String get(Bank prop, String key) {
        return get(prop.name(), key, "");
    }

    public String get(String key, String defalutValue) {
        return get(customPropsKey, key, defalutValue);
    }

    public String get(Bank prop, String key, String defaultValue) {
        return get(prop.name(), key, defaultValue);
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
        return URI.create(getUri(props.get(bank.name()), endpoint, environment) + ending + query);
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
