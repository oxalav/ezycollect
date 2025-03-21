package com.ezycollect.demo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import java.nio.charset.Charset;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

@Service
public class DecryptionServiceImpl implements DecryptionService {
    private final Charset charset = Charset.defaultCharset();
    private final PrivateKey privateKey;

    public DecryptionServiceImpl(@Value("classpath:private-key.asc") Resource privateKeyData) throws Exception {
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(Base64.getMimeDecoder().decode(
                privateKeyData.getContentAsString(charset)
                        .replace("\n", "")
                        .replace("-----BEGIN PRIVATE KEY-----", "")
                        .replace("-----END PRIVATE KEY-----", "")));

        privateKey = KeyFactory.getInstance("RSA").generatePrivate(spec);
    }

    @Override
    public String decrypt(String encrypted) {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);

            byte[] encryptedBytes = Base64.getMimeDecoder().decode(encrypted);
            return new String(cipher.doFinal(encryptedBytes), charset);
        } catch (Exception e) {
            throw new IllegalArgumentException("Decryption failed", e);
        }
    }
}
