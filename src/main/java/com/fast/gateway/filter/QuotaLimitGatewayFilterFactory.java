package com.fast.gateway.filter;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 网关限额过滤器
 */
//@Component
public class QuotaLimitGatewayFilterFactory extends AbstractGatewayFilterFactory<QuotaLimitGatewayFilterFactory.Config> {

    private static RedissonClient redisClient;

    private static ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    @PostConstruct
    public void init() {
        org.redisson.config.Config config = new org.redisson.config.Config();
        config.useReplicatedServers()
                .addNodeAddress("redis://101.43.59.148:6379")
                .setPassword("hust123456");
        redisClient = Redisson.create(config);

        executorService.scheduleAtFixedRate(() -> syncQuotaData(), 1000, 1000, TimeUnit.MILLISECONDS);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {

            return chain.filter(exchange);
        };
    }

    /**
     * 将本地计数的调用和远程进行同步
     */
    private void syncQuotaData() {

    }

    public static class Config {
        private String appId;

    }
}
