package com.example.e2ee_messaging_android_app_frontend.utils;

import java.security.SecureRandom;
import java.util.UUID;

public class SecureUUIDGenerator {
    public static UUID generateSecureUUID() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] randomBytes = new byte[16];
        secureRandom.nextBytes(randomBytes);

        // Set the version and variant bits as per UUID standard
        randomBytes[6] &= 0x0F;
        randomBytes[6] |= 0x40;
        randomBytes[8] &= 0x3F;
        randomBytes[8] |= 0x80;

        long mostSignificantBits = bytesToLong(randomBytes, 0);
        long leastSignificantBits = bytesToLong(randomBytes, 8);

        return new UUID(mostSignificantBits, leastSignificantBits);
    }

    private static long bytesToLong(byte[] bytes, int offset) {
        long value = 0;
        for (int i = 0; i < 8; i++) {
            value = (value << 8) | (bytes[offset + i] & 0xFF);
        }
        return value;
    }
}
