package cz.gyarabProject.api.datatype;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Component
public class Property {
    private final static String propertiesPath = "./src/main/resources/";
    private final Properties props;
    public Property() {
        props = new Properties();
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("api.properties")) {
            props.load(in);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("custom.properties")) {
            props.load(in);
        } catch (IOException e) {
            throw new RuntimeException(e);
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

    /**
     * Create a path like {@link String} from .properties velues from {@code keys}.
     *
     * @param keys for props to find them.
     * @return path like {@link String} from props by keys.
     */
    public String getAbsolutePath(String... keys) {
        StringBuilder path = new StringBuilder();
        for (String key : keys) {
            path.append(props.getProperty(key, ""));
        }
        return path.toString();
    }

    public String getKbUri(String path) {
        return getAbsolutePath("kb.uri", "kb.uri." + path);
    }

    public String getAccountUri() {
        return getAbsolutePath("kb.uri", "kb.uri.account");
    }

    public String getBalanceUri(String accountId) {
        return getUriWithAccount(accountId, "balances");
    }

    public String getTransactionUri(String accountId, Instant from, Instant to, int size, int page) {
        Map<String, String> map = new HashMap<>();
        map.put("account", accountId);
        map.put("from", from.toString());
        map.put("to", to.toString());
        map.put("size", Integer.toString(size));
        map.put("page", Integer.toString(page));
        return getUriWithAccount(accountId, "/transactions") + getQueryParam(map);
    }

    private String getUriWithAccount(String accountId, String type) {
        return getAccountUri() + "/" + accountId + "/" + type;
    }

    private String getQueryParam(Map<String, String> map) {
        StringBuilder query = new StringBuilder("?");
        map.forEach((k, v) -> query.append(k).append("=").append(v).append("&"));
        query.deleteCharAt(query.length() - 1);
        return query.toString();
    }
}
