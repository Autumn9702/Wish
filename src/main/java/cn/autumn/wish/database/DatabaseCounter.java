package cn.autumn.wish.database;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;

/**
 * @author cf
 * Created in 2022/10/27
 */
@Entity(value = "counters", useDiscriminator = false)
public class DatabaseCounter {

    @Id
    private String id;
    private int count;

    public DatabaseCounter() {}

    public DatabaseCounter(String id) {
        this.id = id;
        this.count = 10000;
    }

    public int getNextId() {
        return ++count;
    }
}
