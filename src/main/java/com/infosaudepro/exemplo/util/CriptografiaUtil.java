// src/main/java/com/infosaudepro/exemplo/util/CriptografiaUtil.java
package com.infosaudepro.exemplo.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.nio.charset.StandardCharsets;

/**
 * Utilitário para criptografia e descriptografia AES (ECB/PKCS5Padding).
 */
public class CriptografiaUtil {

    // ➡️ Algoritmo completo para o Cipher.getInstance()
    private static final String FULL_ALGORITHM_SPEC = "AES/ECB/PKCS5Padding";

    // 🔑 Nome do algoritmo BASE para o SecretKeySpec (CORRIGIDO: Apenas "AES")
    private static final String KEY_ALGORITHM_NAME = "AES";

    // 🔐 CHAVE DE 16 BYTES (128 bits): Deve ter 16, 24 ou 32 bytes para AES.
    private static final byte[] KEY = "infosaude-chave!".getBytes(StandardCharsets.UTF_8);

    /**
     * Criptografa um valor de texto.
     */
    public static String encrypt(String value) throws Exception {
        // Usa o nome base "AES"
        SecretKeySpec keySpec = new SecretKeySpec(KEY, KEY_ALGORITHM_NAME);

        Cipher cipher = Cipher.getInstance(FULL_ALGORITHM_SPEC);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        byte[] encryptedValue = cipher.doFinal(value.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encryptedValue);
    }

    /**
     * Descriptografa um valor cifrado.
     */
    public static String decrypt(String encryptedValue) throws Exception {
        // Usa o nome base "AES"
        SecretKeySpec keySpec = new SecretKeySpec(KEY, KEY_ALGORITHM_NAME);

        Cipher cipher = Cipher.getInstance(FULL_ALGORITHM_SPEC);
        cipher.init(Cipher.DECRYPT_MODE, keySpec);
        byte[] decryptedByteValue = Base64.getDecoder().decode(encryptedValue);
        byte[] decryptedValue = cipher.doFinal(decryptedByteValue);
        return new String(decryptedValue, StandardCharsets.UTF_8);
    }
}