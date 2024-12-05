package com.cyberdyne.skynet.Services.Encription;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;

public class EncriptionCLS
{

    private static final String AES_ALGORITHM = "AES/CBC/PKCS5Padding";


    // Generate a secure 256-bit key from a string passphrase
    public static SecretKeySpec generateSecretKey(String passphrase) throws Exception {
        // Use SHA-256 to generate a 256-bit hash from the passphrase
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(passphrase.getBytes(StandardCharsets.UTF_8));

        // Truncate or pad to ensure exactly 32 bytes (256 bits)
        byte[] keyBytes = Arrays.copyOf(hash, 32);

        return new SecretKeySpec(keyBytes, "AES");
    }

    // Encrypt method with string key
    public static String encrypt(String plainText, String key) throws Exception {
        // Generate key from passphrase
        SecretKeySpec secretKey = generateSecretKey(key);

        // Generate IV
        byte[] iv = new byte[16];
        new java.security.SecureRandom().nextBytes(iv);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

        // Encrypt
        Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);
        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

        // Combine IV and encrypted bytes
        byte[] combined = new byte[iv.length + encryptedBytes.length];
        System.arraycopy(iv, 0, combined, 0, iv.length);
        System.arraycopy(encryptedBytes, 0, combined, iv.length, encryptedBytes.length);

        // Base64 encode
        return Base64.getEncoder().encodeToString(combined);
    }

    // Decrypt method with string key
    public static String decrypt(String encryptedText, String key) throws Exception {
        // Generate key from passphrase
        SecretKeySpec secretKey = generateSecretKey(key);

        // Decode from Base64
        byte[] combined = Base64.getDecoder().decode(encryptedText);

        // Extract IV (first 16 bytes)
        byte[] iv = new byte[16];
        System.arraycopy(combined, 0, iv, 0, iv.length);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

        // Extract encrypted bytes
        byte[] encryptedBytes = new byte[combined.length - 16];
        System.arraycopy(combined, 16, encryptedBytes, 0, encryptedBytes.length);

        // Decrypt
        Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

}
