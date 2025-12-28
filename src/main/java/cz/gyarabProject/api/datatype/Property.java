package cz.gyarabProject.api.datatype;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
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
}
