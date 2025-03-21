package com.ezycollect.demo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import java.nio.charset.Charset;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Service
public class EncryptionServiceImpl implements EncryptionService {
    private final Charset charset = Charset.defaultCharset();
    private final PublicKey publicKey;

    public EncryptionServiceImpl(@Value("classpath:public-key.asc") Resource publicKeyData) throws Exception {
        X509EncodedKeySpec spec = new X509EncodedKeySpec(Base64.getMimeDecoder().decode(
                publicKeyData.getContentAsString(charset)
                        .replace("\n", "")
                        .replace("-----BEGIN PUBLIC KEY-----", "")
                        .replace("-----END PUBLIC KEY-----", "")));

        publicKey = KeyFactory.getInstance("RSA").generatePublic(spec);
    }

    @Override
    public String encrypt(String unencrypted) {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);

            byte[] encryptedBytes = cipher.doFinal(unencrypted.getBytes(charset));
            return Base64.getMimeEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            throw new IllegalArgumentException("Decryption failed", e);
        }
    }
}
