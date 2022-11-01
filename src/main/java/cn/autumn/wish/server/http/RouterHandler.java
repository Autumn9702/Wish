package cn.autumn.wish.server.http;

import express.http.Response;

/**
 * @author cf
 * Created in 2022/11/1
 */
public class RouterHandler {

    public static void handlerSuccess(Response response, String message) {
        response.status(200).send(message);
    }

    public static void handlerError(Response response, String message) {
        response.status(700).send(message);
    }

    public static void handlerError(Response response, int status, String message) {
        response.status(status).send(message);
    }
}
