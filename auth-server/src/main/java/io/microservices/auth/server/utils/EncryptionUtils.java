package io.microservices.auth.server.utils;

import org.bouncycastle.util.io.pem.PemReader;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

public final class EncryptionUtils {

    public static PrivateKey readPrivateKey(String keyPath)
            throws NoSuchAlgorithmException, IOException, InvalidKeySpecException {
        final KeyFactory factory = KeyFactory.getInstance("RSA");
        try (Reader reader = new FileReader(keyPath);
             PemReader pemReader = new PemReader(reader)) {
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(pemReader.readPemObject().getContent());
            return factory.generatePrivate(spec);
        }
    }
}
