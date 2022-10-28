package cn.autumn.wish.util;

import cn.autumn.wish.Wish;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author cf
 * Created in 2022/10/28
 */
public final class FileUtil {

    public static byte[] read(String dest) {
        return read(Paths.get(dest));
    }

    public static byte[] read(Path path) {
        try {
            return Files.readAllBytes(path);
        } catch (IOException ioe) {
            Wish.getLogger().warn("Failed to read file: " + path);
        }
        return new byte[0];
    }
}
