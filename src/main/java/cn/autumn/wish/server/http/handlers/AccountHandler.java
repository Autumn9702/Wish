package cn.autumn.wish.server.http.handlers;

import cn.autumn.wish.database.DatabaseHelper;
import cn.autumn.wish.database.entity.Account;
import cn.autumn.wish.server.http.Router;
import cn.autumn.wish.server.http.RouterHandler;
import cn.autumn.wish.util.JsonUtil;
import express.Express;
import express.http.Request;
import express.http.Response;
import io.javalin.Javalin;

import java.util.UUID;

/**
 * @author cf
 * Created in 2022/11/1
 * Handlers all login-related HTTP request.
 */
public final class AccountHandler extends RouterHandler implements Router {
    @Override
    public void applyRouter(Express express, Javalin handle) {
        express.post("/aki/register", AccountHandler::registerAccount);
    }

    private static void registerAccount(Request request, Response response) {
        Account account = JsonUtil.decode(request.get("registerData"), Account.class);
        if (account == null) {
            handlerError(response, "Requested account was not found.");
            return;
        }
        account.setToken(account.generateLoginToken());
        account.setBanned(false);
        account.setId(UUID.randomUUID().toString().replace("-", ""));
        DatabaseHelper.saveAccount(account);
        handlerSuccess(response, "Account register success.");
    }

    private static void loginBackstage(Request request, Response response) {

    }
}
