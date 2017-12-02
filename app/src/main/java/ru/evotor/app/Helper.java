package ru.evotor.app;

import java.security.MessageDigest;
import java.util.Random;

public class Helper {

    public static int rand(int min, int max) {
        return new Random().nextInt(max - min + 1) + min;
    }

    public static long getUnix() {
        return System.currentTimeMillis() / 1000L;
    }

    public static  String md5(final String s) {
        final String MD5 = "MD5";
        try {
            MessageDigest digest = java.security.MessageDigest.getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();
        } catch (Throwable ignored) {}
        return "kek";
    }
}
