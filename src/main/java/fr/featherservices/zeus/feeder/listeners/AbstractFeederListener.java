package fr.featherservices.zeus.feeder.listeners;

import fr.featherservices.zeus.redis.RedisAccess;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;

public abstract class AbstractFeederListener<T> implements IFeedListener<T> {

    private String name;
    private RTopic<T> topic;

    public AbstractFeederListener() {

        this.name = this.getName() + "Feeder";

        RedisAccess redisAccess = RedisAccess.get();
        RedissonClient redissonClient = redisAccess.getClient();

        this.topic = redissonClient.getTopic(this.name);

        this.topic.addListener((message, object) -> this.listen(object));
    }
}
