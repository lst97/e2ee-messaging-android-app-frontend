package com.example.e2ee_messaging_android_app_frontend.helpers;

import android.content.Context;
import android.security.KeyPairGeneratorSpec;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.util.Calendar;

public class KeyStoreHelper {

    private static String KEY_ALIAS = "";

    public static void setKeyAlias(String keyAlias) {
        KEY_ALIAS = keyAlias;
    }

    public static KeyPair generateKeyPair(Context context) throws Exception {
        if (KEY_ALIAS.equals("")) {
            throw new Exception("Key alias not set");
        }

        Calendar startDate = Calendar.getInstance();
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.YEAR, 1);

        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_RSA, "AndroidKeyStore");

        keyPairGenerator.initialize(new KeyGenParameterSpec.Builder(
                KEY_ALIAS, KeyProperties.PURPOSE_SIGN)
                .setDigests(KeyProperties.DIGEST_SHA256)
                .setSignaturePaddings(KeyProperties.SIGNATURE_PADDING_RSA_PSS)
                .build());

        return keyPairGenerator.generateKeyPair();
    }

    public static PublicKey getPublicKey() throws Exception {
        if (KEY_ALIAS.equals("")) {
            throw new Exception("Key alias not set");
        }

        KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyStore.load(null);

        Certificate certificate = keyStore.getCertificate(KEY_ALIAS);
        return certificate.getPublicKey();
    }

    public static PrivateKey getPrivateKey() throws Exception {
        if (KEY_ALIAS.equals("")) {
            throw new Exception("Key alias not set");
        }

        KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyStore.load(null);

        KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry)
                keyStore.getEntry(KEY_ALIAS, null);

        return privateKeyEntry.getPrivateKey();
    }

    public static void deleteKeyPair() throws Exception {
        if (KEY_ALIAS.equals("")) {
            throw new Exception("Key alias not set");
        }

        KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyStore.load(null);

        keyStore.deleteEntry(KEY_ALIAS);
    }
}