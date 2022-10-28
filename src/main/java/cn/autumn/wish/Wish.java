package cn.autumn.wish;

import ch.qos.logback.classic.Logger;
import cn.autumn.wish.config.ConfigContainer;
import cn.autumn.wish.util.Language;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.slf4j.LoggerFactory;

import java.io.IOError;
import java.io.IOException;

import static cn.autumn.wish.config.Configuration.SERVER;

public class Wish {

    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(Wish.class);

    public static Language language;

    private static LineReader consoleLineReader = null;

    public static ConfigContainer config;

    public static Logger getLogger() {
        return LOGGER;
    }

    public static Language getLanguage() {
        return language;
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

    public static void main(String[] args) {

    }

    public static void loop() {

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


    public enum RunMode {
        ALL, DISPATCH, BACKGROUND
    }

    public enum DebugMode {
        ALL, NONE, MISSING
    }

}
