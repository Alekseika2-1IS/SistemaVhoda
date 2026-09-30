package org.example;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class HashParolya {
    // SHA-256 в hex, совпадает с MySQL-функцией SHA2(...,256)
    public static String poluchitHash(String parol) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] baity = md.digest(parol.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : baity) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (Exception e) {
            throw new RuntimeException("Не удалось посчитать хеш пароля", e);
        }
    }
}