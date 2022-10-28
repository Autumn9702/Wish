package cn.autumn.wish.config;

import java.util.Locale;

import static cn.autumn.wish.Wish.config;

/**
 * @author cf
 * Created in 2022/10/27
 */
public final class Configuration extends ConfigContainer{

    /**
     * container
     */

    public static final Locale FALLBACK_LANGUAGE = config.language.fallback;

    public static final Database DATABASE = config.databaseInfo;
    public static final Server SERVER = config.server;

    public static final HTTP HTTP_INFO = config.server.http;
    public static final Backstage BACKSTAGE_INFO = config.server.backstage;
    public static final Dispatch DISPATCH_INFO = config.server.dispatch;

    public static final Encryption HTTP_ENCRYPTION = config.server.http.encryption;
    public static final Policies HTTP_POLICIES = config.server.http.policies;
    public static final Files HTTP_STATIC_FILES = config.server.http.files;

}
