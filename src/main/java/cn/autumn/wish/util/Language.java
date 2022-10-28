package cn.autumn.wish.util;

import cn.autumn.wish.Wish;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import javax.annotation.Nullable;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static cn.autumn.wish.config.Configuration.*;

/**
 * @author cf
 * Created in 2022/10/28
 */
public final class Language {

    private static final Map<String, Language> cachedLanguages = new ConcurrentHashMap<>();

    private final String languageCode;
    private final JsonObject languageData;

    private final Map<String, String> cachedTranslations = new ConcurrentHashMap<>();

    /**
     * Reads a file and created a language instance.
     */
    public Language(LanguageStreamDescription description) {
        @Nullable JsonObject languageData = null;

        languageCode = description.languageCode();

        try {
            languageData = JsonUtil.decode(Utils.readFromInputStream(description.getLanguageFile()), JsonObject.class);
        } catch (Exception e) {
            Wish.getLogger().error("Failed to load language file: " + description.languageCode, e);
        }
        this.languageData = languageData;
    }

    /**
     * Create a language instance from a code
     */
    public static Language getLanguage(String langCode) {
        if (cachedLanguages.containsKey(langCode)) {
            return cachedLanguages.get(langCode);
        }

        var fallbackLanguage = Utils.getLanguageCode(FALLBACK_LANGUAGE);
        var languageFile = getLanguageFile(langCode, fallbackLanguage);
        var actualLanguageCode = languageFile.getLanguageCode();

        Language languageInst;
        if (languageFile.getLanguageFile() != null) {
            languageInst = new Language(languageFile);
            cachedLanguages.put(actualLanguageCode, languageInst);
        } else {
            languageInst = cachedLanguages.get(actualLanguageCode);
            cachedLanguages.put(langCode, languageInst);
        }
        return languageInst;
    }

    /**
     * Returns the translated value from the key while substituting arguments.
     */
    public static String translate(String key, Object... args){
        String translate = Wish.getLanguage().get(key);

        try {
            return translate.formatted(args);
        } catch (Exception e) {
            Wish.getLogger().error("Failed to format string: " + key, e);
        }
        return translate;
    }



    /**
     * create a LanguageStreamDescription
     * @param languageCode The name of the language code.
     * @param fallbackLanguageCode The name of the fallback language code.
     */
    private static LanguageStreamDescription getLanguageFile(String languageCode, String fallbackLanguageCode) {
        var fileName = languageCode + ".json";
        var fallback = fallbackLanguageCode + ".json";

        String actualLanguageCode = languageCode;
        InputStream file = Wish.class.getResourceAsStream("/language/" + fallback);

        if (file == null) {
            Wish.getLogger().warn("Failed to load language file: " + fileName + ", falling back to: " + fallback);
            actualLanguageCode = fallbackLanguageCode;
            if (cachedLanguages.containsKey(actualLanguageCode)) {
                return new LanguageStreamDescription(actualLanguageCode, null);
            }
            file = Wish.class.getResourceAsStream("/language/" + fallback);
        }

        if (file == null) {
            Wish.getLogger().warn("Failed to load language file: " + fallback + ", falling back to: en-US.json");
            actualLanguageCode = "en-US";
            if (cachedLanguages.containsKey(actualLanguageCode)) {
                return new LanguageStreamDescription(actualLanguageCode, null);
            }
            file = Wish.class.getResourceAsStream("/language/en-US.json");
        }

        if (file == null) throw new RuntimeException("Unable to load the primary, fallback and 'en-US' language files.");

        return new LanguageStreamDescription(actualLanguageCode, file);
    }

    /**
     * Returns the value (as a string) from a nested key.
     */
    public String get(String key) {
        if (this.cachedTranslations.containsKey(key)) {
            return this.cachedTranslations.get(key);
        }

        String[] keys = key.split("\\.");
        JsonObject object = this.languageData;

        int index = 0;
        String valueNotFoundPattern = "This value does not exist. Please report this to the Discord: ";
        String result = valueNotFoundPattern + key;
        boolean isValueFound = false;

        while (true) {
            if (index == keys.length) break;

            String currentKey = keys[index++];
            if (object.has(currentKey)) {
                JsonElement element = object.get(currentKey);
                if (element.isJsonObject())
                    object = element.getAsJsonObject();
                else {
                    isValueFound = true;
                    result = element.getAsString();
                    break;
                }
            } else break;
        }

        if (!isValueFound && !languageCode.equals("en-US")) {
            var englishValue = getLanguage("en-US").get(key);
            if (!englishValue.contains(valueNotFoundPattern)) {
                result += "\nhere is english version:\n" + englishValue;
            }
        }

        this.cachedTranslations.put(key, result);
        return result;
    }

    private record LanguageStreamDescription(String languageCode, InputStream languageFile) {

        public String getLanguageCode() {
            return languageCode;
        }

        public InputStream getLanguageFile() {
            return languageFile;
        }

    }
}
