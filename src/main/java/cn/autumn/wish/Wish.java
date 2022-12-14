package cn.autumn.wish;

import ch.qos.logback.classic.Logger;
import cn.autumn.wish.config.ConfigContainer;
import cn.autumn.wish.database.DatabaseManage;
import cn.autumn.wish.server.http.HttpServer;
import cn.autumn.wish.server.http.handlers.AccountHandler;
import cn.autumn.wish.util.JsonUtil;
import cn.autumn.wish.util.Language;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOError;
import java.io.IOException;

import static cn.autumn.wish.config.Configuration.SERVER;

public class Wish {

    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(Wish.class);

    private static final File CONFIG_FILE = new File("./config.json");

    private static HttpServer httpServer;

    public static Language language;

    private static LineReader consoleLineReader = null;

    public static ConfigContainer config;

    public static Logger getLogger() {
        return LOGGER;
    }

    public static Language getLanguage() {
        return language;
    }

    static {

        Wish.loadConfig();


    }

    public static LineReader createConsole() {
        if (consoleLineReader == null) {
            Terminal terminal = null;
            try {
                terminal = TerminalBuilder.builder().jna(true).build();
            } catch (IOException ioe) {
                try {
                    terminal = TerminalBuilder.builder().dumb(true).build();
                } catch (IOException ie) {
                    // When dumb is true, build() never throws.
                }
            }
            consoleLineReader = LineReaderBuilder.builder().terminal(terminal).build();
            return consoleLineReader;
        }
        return null;
    }

    public static void main(String[] args) throws Exception {

        DatabaseManage.initialize();

        httpServer = new HttpServer();

        httpServer.addRouter(AccountHandler.class);

        httpServer.start();

        systemStart();

    }

    public static void systemStart() {

        //  The console does not start in dispatch mode
        if (SERVER.runMode == RunMode.DISPATCH) {
            getLogger().warn("Current dispatch mode.");
            return;
        }

        getLogger().info("Run mode status done.");
        String input = null;
        boolean isLastInterrupted = false;
        while (config.server.backstage.enableConsole) {
            try {
                input = consoleLineReader.readLine("> ");
            } catch (UserInterruptException uie) {
                if (!isLastInterrupted) {
                    isLastInterrupted = true;
                    getLogger().info("Press Ctrl-C again to shutdown.");
                    continue;
                }else {
                    Runtime.getRuntime().exit(0);
                }
            } catch (EndOfFileException eof) {
                getLogger().info("EOF detected.");
                continue;
            } catch (IOError ioErr) {
                getLogger().warn("An IO error occurred.", ioErr);
            }

            isLastInterrupted = false;

        }
    }

    /**
     * Load the configuration from a file.
     */
    public static void loadConfig() {
        if (!CONFIG_FILE.exists()) {
            getLogger().info("'config.json' could not be found. Generating a default configuration ...");
            return;
        }

        try {
            config = JsonUtil.loadToClass(CONFIG_FILE.getPath(), ConfigContainer.class);
        } catch (Exception e) {
            getLogger().error("There was an error while trying to load the configuration from config.json. Please make sure that there are no syntax errors. If you want to start with a default configuration, delete your existing config.json.");
            System.exit(1);
        }
    }

    public enum RunMode {
        ALL, DISPATCH, BACKGROUND
    }

    public enum DebugMode {
        ALL, NONE, MISSING
    }

}
