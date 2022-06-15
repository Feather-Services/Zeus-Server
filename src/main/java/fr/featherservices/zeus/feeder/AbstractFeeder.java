package fr.featherservices.zeus.feeder;

import fr.featherservices.zeus.redis.RedisAccess;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;

public abstract class AbstractFeeder<T> implements IFeeder<T> {
    private String name;
    private RTopic<T> topic;

    public AbstractFeeder() {

        this.name = this.getName() + "Feeder";

        RedisAccess redisAccess = RedisAccess.get();
        RedissonClient redissonClient = redisAccess.getClient();

        this.topic = redissonClient.getTopic(this.name);
    }

    @Override
    public void publish() {
        try {
            this.topic.publish(this.feed());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
