package cn.autumn.wish.server.http;

import cn.autumn.wish.Wish;
import cn.autumn.wish.Wish.DebugMode;
import cn.autumn.wish.util.FileUtil;
import express.Express;
import express.http.MediaType;
import io.javalin.Javalin;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.ssl.SslContextFactory;

import java.io.File;
import java.io.UnsupportedEncodingException;

import static cn.autumn.wish.config.Configuration.*;
import static cn.autumn.wish.util.Language.translate;

/**
 * @author cf
 * Created in 2022/10/28
 */
public final class HttpServer {

    private final Express express;

    /**
     * Configures the express application
     */
    public HttpServer() {
        this.express = new Express(config -> {
            // Set Express http server
            config.server(HttpServer::createServer);
            // Configure encryption/HTTP/SSL
            config.enforceSsl = HTTP_ENCRYPTION.useEncryption;
            // Configure HTTP policies/Cross domain
            if (HTTP_POLICIES.cors.enabled) {
                var allowedOrigins = HTTP_POLICIES.cors.allowedOrigins;
                if (allowedOrigins.length > 0) {
                    config.enableCorsForOrigin(allowedOrigins);
                } else config.enableCorsForAllOrigins();
            }

            // Configure debug log mode.
            if (DISPATCH_INFO.logMode == Wish.DebugMode.ALL) {
                config.enableDevLogging();
            }

            // When configure the static files.
            config.precompressStaticFiles = false;
        });
    }

    /**
     * Creates an HTTP/HTTPS server
     * @return A server instance
     */
    @SuppressWarnings("resource")
    private static Server createServer() {

        Server server = new Server();
        ServerConnector serverConnector = new ServerConnector(server);

        if (HTTP_ENCRYPTION.useEncryption) {

            var sslContextFactory = new SslContextFactory.Server();
            var keyStoreFile = new File(HTTP_ENCRYPTION.keystore);

            if (!keyStoreFile.exists()) {

                HTTP_ENCRYPTION.useEncryption = false;
                HTTP_ENCRYPTION.useInRouting = false;

                Wish.getLogger().warn("No keystore.");

            } else try {
                sslContextFactory.setKeyStorePath(keyStoreFile.getPath());
                sslContextFactory.setKeyStorePassword(HTTP_ENCRYPTION.keystorePassword);
            } catch (Exception e) {

                Wish.getLogger().warn("Configures keystore password error.");

                try {
                    sslContextFactory.setKeyStorePath(keyStoreFile.getPath());
                    sslContextFactory.setKeyStorePassword("123456");
                    Wish.getLogger().info("Setter default keystore password.");
                }catch (Exception de) {
                    Wish.getLogger().warn("Keystore general error: " + de);
                }
            } finally {
                serverConnector = new ServerConnector(server, sslContextFactory);
            }
        }

        serverConnector.setPort(HTTP_INFO.bindPort);
        server.setConnectors(new ServerConnector[]{serverConnector});

        return server;
    }

    /**
     * Initializes associated with class for router interface.
     * @param router Associated with for router interface.
     * @return Method chaining
     */
    @SuppressWarnings("UnusedReturnValue")
    public HttpServer addRouter(Class<? extends Router> router, Object... args) {
        // Get all constructor parameters
        Class<?>[] types = new Class<?>[args.length];
        for (var parameter : args) {
            types[args.length -1] = parameter.getClass();
        }
        // Create a router instance & apply routers
        try {
            var constructor = router.getDeclaredConstructor(types);
            var instance = constructor.newInstance(args);
            instance.applyRouter(this.express, this.handle());
        } catch (Exception e) {
            Wish.getLogger().warn("Router error.");
        }
        return this;
    }

    /**
     * Returns the handle for the express application.
     * @return A {@link Javalin} instance.
     */
    public Javalin handle() {
        return this.express.raw();
    }

    /**
     * Starts listening on the HTTP server.
     * @throws UnsupportedEncodingException
     */
    public void start() throws UnsupportedEncodingException {
        if (HTTP_INFO.bindAddress.equals("")) {
            this.express.listen(HTTP_INFO.bindPort);
        } else this.express.listen(HTTP_INFO.bindAddress, HTTP_INFO.bindPort);
        // Log bind information
        Wish.getLogger().info("Port bind: " + this.express.raw().port());
    }

    /**
     * Handle the '/' or '/index' endpoint on the express application.
     */
    public static class DefaultRequestRouter implements Router {

        @Override public void applyRouter(Express express, Javalin handle) {
            express.get("/", (request, response) -> {
                File file = new File(HTTP_STATIC_FILES.indexFile);
                if (!file.exists()) {
                    response.send("""
                            <!DOCTYPE html>
                            <html>
                                <head>
                                    <meta charset="utf8">
                                </head>
                                <body>%s</body>
                            </html>
                            """.formatted(translate("messages.status.welcome")));
                } else {
                    final var filePath = file.getPath();
                    final MediaType fromExtension = MediaType.getByExtension(filePath.substring(filePath.lastIndexOf(".") +1));
                    response.type((fromExtension != null) ? fromExtension.getMIME() : "text/plain").send(FileUtil.read(filePath));
                }
            });
        }
    }

    /**
     * Handles unhandled endpoints on the Express application.
     */
    public static class UnHandleRequestRouter implements Router {

        @Override
        public void applyRouter(Express express, Javalin handle) {
            handle.error(404, context -> {
                if (DISPATCH_INFO.logMode == DebugMode.MISSING)
                    Wish.getLogger().info(translate("messages.dispatch.unhandled_request_error", context.method(), context.url()));
                context.contentType("text/html");
                File file = new File(HTTP_STATIC_FILES.errorFile);
                if (!file.exists())
                    context.result("""
                        <!DOCTYPE html>
                        <html>
                            <head>
                                <meta charset="utf8">
                            </head>

                            <body>
                                <img src="https://http.cat/404" />
                            </body>
                        </html>
                        """);
                else {
                    final var filePath = file.getPath();
                    final MediaType fromExtension = MediaType.getByExtension(filePath.substring(filePath.lastIndexOf(".") + 1));
                    context.contentType((fromExtension != null) ? fromExtension.getMIME() : "text/plain")
                            .result(FileUtil.read(filePath));
                }
            });
        }
    }
}
