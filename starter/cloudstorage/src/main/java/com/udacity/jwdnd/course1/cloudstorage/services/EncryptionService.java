package com.udacity.jwdnd.course1.cloudstorage.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.BadPaddingException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

@Service
public class EncryptionService {
    private final Logger logger = LoggerFactory.getLogger(EncryptionService.class);

    private static final String V2_PREFIX = "v2:"; // marker for GCM payloads
    private static final int GCM_TAG_BITS = 128;
    private static final int GCM_IV_BYTES = 12; // 96-bit IV

    public String encryptValue(String data, String key) {
        try {
            // v2: AES/GCM with random IV; prefix the output with version marker
            byte[] iv = new byte[GCM_IV_BYTES];
            new SecureRandom().nextBytes(iv);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            SecretKey secretKey = new SecretKeySpec(normalizeKey(key), "AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, new GCMParameterSpec(GCM_TAG_BITS, iv));
            byte[] ciphertext = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));

            byte[] combined = new byte[iv.length + ciphertext.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(ciphertext, 0, combined, iv.length, ciphertext.length);
            return V2_PREFIX + Base64.getEncoder().encodeToString(combined);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException |
                 InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
            logger.error("Encryption failed: {}", e.getMessage());
            throw new IllegalStateException("Encryption failed", e);
        }
    }

    public String decryptValue(String data, String key) {
        try {
            if (data != null && data.startsWith(V2_PREFIX)) {
                // v2: AES/GCM with IV prepended
                String b64 = data.substring(V2_PREFIX.length());
                byte[] combined = Base64.getDecoder().decode(b64);
                if (combined.length < GCM_IV_BYTES + 1) {
                    throw new IllegalArgumentException("Ciphertext too short");
                }
                byte[] iv = new byte[GCM_IV_BYTES];
                byte[] ciphertext = new byte[combined.length - GCM_IV_BYTES];
                System.arraycopy(combined, 0, iv, 0, GCM_IV_BYTES);
                System.arraycopy(combined, GCM_IV_BYTES, ciphertext, 0, ciphertext.length);

                Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
                SecretKey secretKey = new SecretKeySpec(normalizeKey(key), "AES");
                cipher.init(Cipher.DECRYPT_MODE, secretKey, new GCMParameterSpec(GCM_TAG_BITS, iv));
                byte[] plain = cipher.doFinal(ciphertext);
                return new String(plain, StandardCharsets.UTF_8);
            }

            // v1 fallback: AES/ECB/PKCS5Padding (legacy)
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            SecretKey secretKey = new SecretKeySpec(normalizeKey(key), "AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decryptedValue = cipher.doFinal(Base64.getDecoder().decode(data));
            return new String(decryptedValue, StandardCharsets.UTF_8);
        } catch (Exception e) {
            logger.error("Decryption failed: {}", e.getMessage());
            throw new IllegalStateException("Decryption failed", e);
        }
    }

    private byte[] normalizeKey(String key) {
        // Ensure 16-byte key for AES-128; pad with zeros or truncate
        byte[] src = key == null ? new byte[0] : key.getBytes(StandardCharsets.UTF_8);
        byte[] out = new byte[16];
        int len = Math.min(src.length, 16);
        System.arraycopy(src, 0, out, 0, len);
        // remaining bytes are already zeros
        return out;
    }
}
