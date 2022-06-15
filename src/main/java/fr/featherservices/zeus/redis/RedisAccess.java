package fr.featherservices.zeus.redis;

import fr.featherservices.zeus.ZeusServer;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;

public class RedisAccess {

    private static RedisAccess instance;
    private RedissonClient client;

    public RedisAccess(RedisCredentials credentials) {
        this.client = this.initRedisson(credentials);

        instance = this;
    }

    public RedissonClient initRedisson(RedisCredentials credentials) {
        Config config = new Config();
        config.setCodec(new JsonJacksonCodec());
        config.setUseLinuxNativeEpoll(true);
        config.setThreads(4);
        config.setNettyThreads(4);
        config.useSingleServer()
                .setConnectionPoolSize(5)
                .setAddress(credentials.toURI())
                .setPassword(credentials.getPassword())
                .setDatabase(1)
                .setClientName(credentials.getClient());

        return Redisson.create(config);
    }

    public static void init() {
        new RedisAccess(new RedisCredentials(ZeusServer.getInstance().getServerProperties().getRedisHost(), ZeusServer.getInstance().getServerProperties().getPassword(), ZeusServer.getInstance().getServerProperties().getRedisPort()));
        ZeusServer.getInstance().getConsole().sendMessage("Connecting to Redis");
    }

    public static void close() {
        RedisAccess.get().getClient().shutdown();
        ZeusServer.getInstance().getConsole().sendMessage("Disconnecting from Redis");
    }

    public RedissonClient getClient() {
        return this.client;
    }

    public static RedisAccess get() {
        return instance;
    }
}
