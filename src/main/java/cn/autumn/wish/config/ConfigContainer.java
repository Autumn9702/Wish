package cn.autumn.wish.config;

import cn.autumn.wish.Wish.DebugMode;
import cn.autumn.wish.Wish.RunMode;

import java.util.Locale;

/**
 * @author cf
 * Created in 2022/10/27
 */
public class ConfigContainer {

    public Database databaseInfo = new Database();
    public Language language = new Language();


    public Server server = new Server();


    public static class Database {
        public DataStore server = new DataStore();
        public DataStore mongo = new DataStore();

        public static class DataStore {
            public String connectionUri = "mongodb://mongo:chengzi@localhost:27030";
            public String collection = "wish";
        }
    }

    public static class Server {

        public RunMode runMode = RunMode.ALL;

        public HTTP http = new HTTP();
        public Backstage backstage = new Backstage();

        public Dispatch dispatch = new Dispatch();

    }

    public static class Language {
        public Locale language = Locale.getDefault();
        public Locale fallback = Locale.US;
        public String document = "EN";
    }

    /**
     * Data container
     */
    public static class Dispatch {

        private String defaultName = "Wish";

        public DebugMode logMode = DebugMode.NONE;

    }

    public static class HTTP {

        public String bindAddress = "0.0.0.0";
        public int bindPort = 9050;

        /* This is the address used in URLs. */
        public String accessAddress = "127.0.0.1";
        /* This is the port used in URLs. */
        public int accessPort = 0;

        public Encryption encryption = new Encryption();
        public Policies policies = new Policies();
        public Files files = new Files();

    }

    /**
     * The console mode.
     */
    public static class Backstage {
        public String bindAddress = "0.0.0.0";
        public int bindPort = 22102;

        /* This is the address used in the default region. */
        public String accessAddress = "127.0.0.1";
        /* This is the port used in the default region. */
        public int accessPort = 0;

        public boolean enableConsole = true;
    }

    /**
     * Encrypt http request.
     */
    public static class Encryption {
        public boolean useEncryption = true;
        /* Should 'https' be appended to URLs? */
        public boolean useInRouting = true;
        public String keystore = "./keystore.p12";
        public String keystorePassword = "123456";
    }

    /**
     * Cross domain.
     */
    public static class Policies {
        public Policies.CORS cors = new Policies.CORS();

        public static class CORS {
            public boolean enabled = false;
            public String[] allowedOrigins = new String[]{"*"};
        }
    }

    public static class Files {
        public String indexFile = "./index.html";
        public String errorFile = "./404.html";
    }


    public static class Local {

        public Local() {}

        public Local(String name, String title, String ip, int port) {
            this.name = name;
            this.title = title;
            this.ip = ip;
            this.port = port;
        }

        private String name;
        private String title;
        private String ip;
        private int port;
    }
}
