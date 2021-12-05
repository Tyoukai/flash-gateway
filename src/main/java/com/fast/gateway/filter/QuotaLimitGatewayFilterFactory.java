package com.fast.gateway.filter;

import com.fast.gateway.others.QuotaLimitHelper;
import com.fast.gateway.utils.GatewayContextUtils;
import com.fast.gateway.utils.RewriteResponseUtils;
import com.google.common.base.Strings;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.fast.gateway.utils.Constants.SPLIT_COLON;
import static com.fast.gateway.utils.Constants.SPLIT_COMMA;
import static com.fast.gateway.utils.Constants.SPLIT_SEMICOLON;

/**
 * 网关限额过滤器
 */
//@Component
public class QuotaLimitGatewayFilterFactory extends AbstractGatewayFilterFactory<QuotaLimitGatewayFilterFactory.Config> {

    private static RedissonClient redisClient;

    private static ScheduledExecutorService executorService = Executors.newScheduledThreadPool(2);

    private QuotaLimitHelper quotaLimitHelper;

    @PostConstruct
    public void init() {
        // 1、初始化redis
        org.redisson.config.Config config = new org.redisson.config.Config();
        config.useReplicatedServers()
                .addNodeAddress("redis://101.43.59.148:6379")
                .setPassword("hust123456");
        redisClient = Redisson.create(config);

        // 2、初始化本地计数器
        quotaLimitHelper = QuotaLimitHelper.getInstance();

        // 3、初始化远程同步任务
        executorService.scheduleAtFixedRate(() -> quotaLimitHelper.pullRemoteToLocalAndPushLocalToRemote(redisClient), 1000, 1000, TimeUnit.MILLISECONDS);

        // 4、删除本地过期的限额配置
        executorService.scheduleAtFixedRate(() -> quotaLimitHelper.removeLocalExpiredQuotaConfig(), 2000, 2000, TimeUnit.MICROSECONDS);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String[] keys = Strings.nullToEmpty(config.getKeys()).split(SPLIT_SEMICOLON);
            for (String key : keys) {
                String realKey = buildKey(key, exchange);
                if (!quotaLimitHelper.tryAcquire(realKey, 1)) {
                    return RewriteResponseUtils.rewriteResponse(exchange, "当前api超过额度限制");
                }
            }
            return chain.filter(exchange);
        };
    }

    private String buildKey(String key, ServerWebExchange exchange) {
        String[] keyArray = Strings.nullToEmpty(key).split(SPLIT_COMMA);
        return Arrays.stream(keyArray)
                .map(k -> k.trim() + SPLIT_COLON + GatewayContextUtils.getParam(exchange, k))
                .sorted(String::compareTo)
                .collect(Collectors.joining(SPLIT_SEMICOLON));
    }

    public static class Config {
        private String keys;

        public String getKeys() {
            return keys;
        }

        public void setKeys(String keys) {
            this.keys = keys;
        }
    }
}
