package cn.autumn.wish.server.http;

import express.Express;
import io.javalin.Javalin;

/**
 * @author cf
 * Created in 2022/10/28
 * Defines routes for an {@link Express} instance.
 */
public interface Router {

    /**
     * Called when the router is initialized by Express.
     * @param express An Express instance.
     */
    void applyRouter(Express express, Javalin handle);

}
