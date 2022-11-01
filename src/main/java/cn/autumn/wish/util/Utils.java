package cn.autumn.wish.util;

import cn.autumn.wish.Wish;

import javax.annotation.Nullable;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

/**
 * @author cf
 * Created in 2022/10/28
 */
@SuppressWarnings({"UnusedReturnValue", "BooleanMethodIsAlwaysInverted"})
public final class Utils {


    /**
     * Gets the language code from a given locale.
     * @param locale A locale.
     * @return A string in the format of 'XX-XX'.
     */
    public static String getLanguageCode(Locale locale) {
        return String.format("%s-%s", locale.getLanguage(), locale.getCountry());
    }

    /**
     * Retrieves a string from an input stream.
     */
    public static String readFromInputStream(@Nullable InputStream stream) {
        if (stream == null) return "empty";

        StringBuilder stringBuilder = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            stream.close();
        } catch (IOException ioe) {
            Wish.getLogger().warn("Failed to read from input stream.");
        } catch (NullPointerException ignored) {
            return "empty";
        }
        return stringBuilder.toString();
    }

    private static final char[] HEX_ARRAY = "0123456789abcdef".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        if (bytes == null) return "";
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }
}
