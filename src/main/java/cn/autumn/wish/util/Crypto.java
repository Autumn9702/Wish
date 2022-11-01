package cn.autumn.wish.util;

import java.security.SecureRandom;

/**
 * @author cf
 * Created in 2022/10/31
 */
public final class Crypto {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();



    public static byte[] createSessionKey(int length) {
        byte[] bytes = new byte[length];
        SECURE_RANDOM.nextBytes(bytes);
        return bytes;
    }
}
