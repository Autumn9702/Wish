package cn.autumn.wish.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * @author cf
 * Created in 2022/10/28
 */
public final class JsonUtil {
    static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Safely JSON decodes a given string.
     */
    public static <T> T decode(String jsonData, Class<T> type) {
        try {
            return GSON.fromJson(jsonData, type);
        } catch (Exception ignore) {
            return null;
        }
    }

    public static <T> T loadToClass(InputStreamReader fileReader, Class<T> type) {
        return GSON.fromJson(fileReader, type);
    }

    public static <T> T loadToClass(String fileName, Class<T> type) throws IOException {
        try (InputStreamReader reader = new InputStreamReader(new FileInputStream(Utils.filePath(fileName)), StandardCharsets.UTF_8)) {
            return loadToClass(reader, type);
        }
    }
}
