package Decryption;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Implementation of the {@link Decryption.ResponseDecryption}.
 *
 * @author <a href="mailto:aleh_kuchynski@kb.cz">Aleh Kuchynski</a>
 * @see Decryption.ResponseDecryption
 * @since 1.0
 */
@Service
public class ResponseDecryption {
    private static final String AES_ALGORITHM_NAME = "AES";
    private static final int KEY_BYTE_SIZE = 32;
    private static final int KEY_OFFSET = 0;
    private static final int AUTHENTICATION_TAG_BIT_SIZE = 128;

    /**
     * Decrypt {@code cipherText} to {@link String} by encryptionKey and IV.
     *
     * @param cipherText crypted text.
     * @param iv iv (or salt) for decryption.
     * @param encryptionKey key for dercrypting {@code cipherText}.
     *
     * @return JSON like {@link String} as decrypted value.
     */
    public static String decrypt(String cipherText, String iv, String encryptionKey) {
        Assert.hasText(cipherText, "base64UrlText must not be empty");
        Assert.hasText(iv, "base64UrlText must not be empty");
        Assert.hasText(encryptionKey, "secret must not be empty");

        byte[] decrypted;
        try {
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            GCMParameterSpec parameterSpec = new GCMParameterSpec(AUTHENTICATION_TAG_BIT_SIZE, base64Decode(iv));
            cipher.init(Cipher.DECRYPT_MODE, decodeSecretKey(encryptionKey), parameterSpec);

            decrypted = cipher.doFinal(base64Decode(cipherText));
        } catch (Exception e) {
            throw new IllegalStateException("Ciphered text could not be decrypted.", e);
        }

        return new String(decrypted, StandardCharsets.UTF_8);
    }

    /**
     * Decodes Base64Url encoded text.
     *
     * @param  base64UrlText encoded text
     * @return decoded text
     */
    private static byte[] base64Decode(String base64UrlText) {
        Assert.hasText(base64UrlText, "base64UrlText must not be empty");

        return Base64.getUrlDecoder().decode(base64UrlText.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Decodes {@link Base64} secret {@code key}.
     *
     * @param key {@link Base64} encoded encryption key.
     * @return SecretKey from {@code key}.
     */
    private static SecretKey decodeSecretKey(String key) {
        Assert.hasText(key, "key must not be empty");

        try {
            return new SecretKeySpec(Base64.getDecoder().decode(key.getBytes(StandardCharsets.UTF_8)),
                    KEY_OFFSET, KEY_BYTE_SIZE, AES_ALGORITHM_NAME);
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new IllegalStateException("Cannot get/decode encryption key.", e);
        }
    }
}