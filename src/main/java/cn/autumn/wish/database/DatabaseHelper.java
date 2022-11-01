package cn.autumn.wish.database;

import cn.autumn.wish.database.entity.Account;
import cn.autumn.wish.database.entity.User;
import dev.morphia.query.experimental.filters.Filters;

/**
 * @author cf
 * Created in 2022/10/31
 */
public final class DatabaseHelper {

    public static Account queryAccountById(String id) {
        return DatabaseManage.getMongoDatastore().find(Account.class).filter(Filters.eq("id", id)).first();
    }
    public static User queryUserById(int id) {
        return DatabaseManage.getMongoDatastore().find(User.class).filter(Filters.eq("id", id)).first();
    }

    public static void saveAccount(Account account) {
        DatabaseManage.getMongoDatastore().save(account);
    }
    public static void saveUser(User user) {
        DatabaseManage.getMongoDatastore().save(user);
    }
}
