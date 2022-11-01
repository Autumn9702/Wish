package cn.autumn.wish.commands;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author cf
 * Created in 2022/10/31
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {

    String label() default "";

    String[] aliases() default {};

    String[] usage() default {""};

    String permission() default "";

    String permissionTargeted() default "";

    boolean threading() default false;

    enum TargetRequirement {
        NONE, PLAYER, OFFLINE, ONLINE
    }

    TargetRequirement targetRequirement() default TargetRequirement.ONLINE;

}
