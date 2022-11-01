package cn.autumn.wish.database;

import cn.autumn.wish.database.entity.Account;

/**
 * @author cf
 * Created in 2022/10/31
 */
public final class DatabaseHelper {

    public static void saveAccount(Account account) {
        DatabaseManage.getMongoDatastore().save(account);
    }

}
