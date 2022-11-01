package cn.autumn.wish.database.entity;

import cn.autumn.wish.database.DatabaseHelper;
import cn.autumn.wish.util.Crypto;
import cn.autumn.wish.util.Utils;
import dev.morphia.annotations.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static cn.autumn.wish.config.Configuration.LANGUAGE;

/**
 * @author cf
 * Created in 2022/10/31
 */
@Entity(value = "account", useDiscriminator = false)
public class Account {

    @Getter @Setter @Id private String id;

    @Indexed(options = @IndexOptions(unique = true))
    @Collation(locale = "simple", caseLevel = true)
    @Getter @Setter private String username;
    @Getter @Setter private String password;

    @Setter private String email;
    @Setter private String phone;

    @Getter @Setter private String token;

    @Setter private boolean isBanned;
    @Getter @Setter private int banStartTime;
    @Getter @Setter private int banEndTime;
    private String banReason;

    @Getter private List<String> permissions;
    @Getter @Setter private Locale locale;

    @Deprecated
    public Account() {
        this.permissions = new ArrayList<>();
        this.locale = LANGUAGE;
    }

    public String getEmail() {
        if (email != null && !email.isEmpty()) {
            return email;
        }
        return "";
    }

    public boolean isBanned() {
        if (banEndTime > 0 && banEndTime < System.currentTimeMillis() / 1000) {
            this.isBanned = false;
            this.banStartTime = 0;
            this.banEndTime = 0;
            this.banReason = null;
            save();
        }
        return this.isBanned;
    }

    public boolean addPermission(String permission) {
        if (this.permissions.contains(permission)) return false;
        this.permissions.add(permission);
        return true;
    }

    public String generateLoginToken() {
        this.token = Utils.bytesToHex(Crypto.createSessionKey(32));
        this.save();
        return this.token;
    }

    public void save() {
        DatabaseHelper.saveAccount(this);
    }
}
