package com.atlasdblite.security;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

public class CryptoManager {
    private static final String ALGORITHM = "AES";
    private static final String KEY_FILE = "atlas.key";
    private SecretKey secretKey;

    public CryptoManager() {
        try {
            this.secretKey = loadOrGenerateKey();
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize security layer: " + e.getMessage());
        }
    }

    public String encrypt(String data) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedBytes = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public String decrypt(String encryptedData) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decodedBytes = Base64.getDecoder().decode(encryptedData);
        return new String(cipher.doFinal(decodedBytes));
    }

    private SecretKey loadOrGenerateKey() throws Exception {
        File keyFile = new File(KEY_FILE);
        if (keyFile.exists()) {
            byte[] encodedKey = Files.readAllBytes(Paths.get(KEY_FILE));
            return new SecretKeySpec(encodedKey, ALGORITHM);
        } else {
            System.out.println(" [SECURITY] Generating new AES-256 encryption key...");
            KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
            keyGen.init(256);
            SecretKey key = keyGen.generateKey();
            Files.write(Paths.get(KEY_FILE), key.getEncoded());
            return key;
        }
    }
}