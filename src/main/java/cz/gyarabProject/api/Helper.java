package cz.gyarabProject.api;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Base64;
import java.util.Properties;
import java.util.stream.Collectors;

public class Helper {

    /**
     * Check if {@code string} is null or empty.
     *
     * @param string {@link String} to be checked.
     * @return {@code boolean} if the {@code string} is null or empty.
     */
    public static boolean isNullOrEmpty(String string) {
        return string == null || string.isEmpty();
    }

    /**
     * Read the given file and return its content. The file have to exist and cannot be empty.
     *
     * @param path to the file.
     * @return content of the given file.
     * @throws IOException when the {@code path} doesn't exist or file is empty.
     */
    public static String readValidFile(String path) throws IOException {
        String file = readFile(path);
        if (isNullOrEmpty(file)) {
            throw new IOException("File not found or is empty: " + path);
        }
        return file;
    }

    /**
     * Read the given file.
     *
     * @param path to the file.
     * @return {@link String} when the file exist and {@code null} when the file doesn't exist.
     */
    public static String readFile(String path) {
        try {
            File file = new File(path);
            if (!file.exists()) return null;
            return Files.readString(file.toPath());
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Create new file with given {@code path} and insert into the {@code content}. If the file allready exist, it will throw {@link IOException}.
     *
     * @param path to the file.
     * @param content the content to be written into the file.
     * @throws IOException when the file allready exist.
     */
    public static void createAndWriteFile(String path, String content) throws IOException {
        File file = new File(path);
        if (!file.exists()) {
            file.createNewFile();
            new FileOutputStream(file).write(content.getBytes());
            return;
        }
        throw new IOException("File allready exists.");
    }

    /**
     * Check if the given {@code json} is valid JSON.
     *
     * @param json {@link String} to be checked.
     * @return true if the {@code json} is valid JSON.
     */
    public static boolean isValidJson(String json) {
        try {
            new JSONObject(json);
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    /**
     * Cut the given array to the maximum {@code amount} of elemets.
     *
     * @param string array of {@code String[]} to be cutted
     * @param amount how many should be in the returned array.
     * @return the cutted array.
     */
    public static String[] cutArray(String[] string, int amount) {
        if (string.length > amount) {
            var arr = new String[amount];
            System.arraycopy(string, 0, arr, 0, amount);
            return arr;
        }
        return string;
    }

    /**
     * Convert the {@code string} from the .properties files to JSON like Array as {@link String}.
     *
     * @param string {@link String} from the .properties files to be converted.
     * @param separator {@link String} that seperates the elements for the array in given {@link String}.
     * @return JSON like Array as {@link String} converted from the given {@code string}.
     */
    public static String fromStringToArray(String string, String separator) {
        return Arrays.stream(cutArray(string.split(separator), 2)).map(s -> "\"" + s + "\"")
                .collect(Collectors.joining(",", "[", "]"));
    }

    /**
     * Convert the {@code string} from the .properties files to JSON like Array as {@link String}
     * and add the adder infront of every element.
     *
     * @param string {@link String} from the .properties files to be converted.
     * @param separator {@link String} that {@code seperates} the elements for the array in given {@link String}.
     * @param adder {@link String} that is added infront of every element in array.
     * @return JSON like Array as {@link String} converted from the given string.
     */
    public static String fromStringToArray(String string, String separator, String adder) {
        return Arrays.stream(cutArray(string.split(separator), 2)).map(s -> "\"" + adder + s + "\"")
                .collect(Collectors.joining(",", "[", "]"));
    }

    /**
     * Create a path like {@link String} from .properties velues from {@code keys}.
     *
     * @param props {@link Properties} to find {@code keys}.
     * @param keys for props to find them.
     * @return path like {@link String} from props by keys.
     */
    public static String getAbsolutePath(Properties props, String... keys) {
        StringBuilder path = new StringBuilder();
        for (String key : keys) {
            path.append(props.getProperty(key, ""));
        }
        return path.toString();
    }

    /**
     * Encode a {@code string} to a {@link Base64} format and return it as a {@code byte[]}.
     *
     * @param string {@link String} to be encoded.
     * @return endoded {@link Base64} value.
     */
     public static byte[] encodeBase64(String string) {
        return Base64.getEncoder().encode(string.getBytes());
     }

    /**
     * Encode a {@code base64} to a {@link Base64} format and return it as a {@code byte[]}.
     *
     * @param base64 {@code byte[]} to be encoded.
     * @return endoded {@link Base64} value.
     */
    public static byte[] encodeBase64(byte[] base64) {
        return Base64.getEncoder().encode(base64);
    }

    /**
     * Decode a {@code string} from a {@link Base64} format and return it as a {@code byte[]}.
     *
     * @param string {@link String} to be decoded.
     * @return endoded {@link Base64} value.
     */
    public static byte[] decodeBase64(String string) {
        return Base64.getDecoder().decode(string.getBytes());
    }

    /**
     * Decode a {@code base64} from a {@link Base64} format and return it as a {@code byte[]}.
     *
     * @param base64 {@code byte[]} to be decoded.
     * @return endoded {@link Base64} value.
     */
     public static byte[] decodeBase64(byte[] base64) {
        return Base64.getDecoder().decode(base64);
     }
}
