package cn.autumn.wish.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author cf
 * Created in 2022/10/28
 */
public final class JsonUtil {
    static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Safely JSON decodes a given string.
     */
    public static <T> T decode(String jsonData, Class<T> classType) {
        try {
            return GSON.fromJson(jsonData, classType);
        } catch (Exception ignore) {
            return null;
        }
    }
}
