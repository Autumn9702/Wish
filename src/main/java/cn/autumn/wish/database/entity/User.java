package cn.autumn.wish.database.entity;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import lombok.Getter;
import lombok.Setter;

/**
 * @author cf
 * Created in 2022/10/31
 */
@Entity(value = "user", useDiscriminator = false)
public final class User {
    @Getter @Setter @Id private int id;
    @Setter private transient Account account;

    @Getter @Setter private String nickName;
    @Getter @Setter private String signature;
    @Getter @Setter private String headImage;

}
