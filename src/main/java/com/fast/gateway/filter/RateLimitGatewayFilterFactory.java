package com.fast.gateway.filter;

import com.fast.gateway.others.RateLimitHelper;
import com.fast.gateway.utils.GatewayContextUtils;
import com.fast.gateway.utils.RewriteResponseUtils;
import com.google.common.base.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.fast.gateway.utils.Constants.SPLIT_SEMICOLON;
import static com.fast.gateway.utils.Constants.UNKNOW;

/**
 * 限流Filter
 */
@Component
public class RateLimitGatewayFilterFactory extends AbstractGatewayFilterFactory<RateLimitGatewayFilterFactory.Config> {

    @Autowired
    private RateLimitHelper rateLimitHelper;

    private static ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);


    public RateLimitGatewayFilterFactory() {
        super(Config.class);
    }

    @PostConstruct
    public void init() {
        // 1、初始化同步数据库同步任务
        executorService.scheduleAtFixedRate(() -> rateLimitHelper.syncQpsInDataBase(), 0, 1, TimeUnit.SECONDS);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String[] keys = Strings.nullToEmpty(config.getKeys()).split(SPLIT_SEMICOLON);
            for (String key : keys) {
                String realKey = GatewayContextUtils.buildKey(exchange, key);
                if (realKey.contains(UNKNOW)) {
                    continue;
                }

                if (!rateLimitHelper.tryAcquire(realKey)) {
                    return RewriteResponseUtils.rewriteResponse(exchange, "当前api超过流量限制");
                }
            }

            return chain.filter(exchange);
        };
    }

    public static class Config {
        private String keys;

        public String getKeys() {
            return keys;
        }

        public void setKeys(String keys) {
            this.keys = keys;
        }

        @Override
        public String toString() {
            return super.toString();
        }
    }
}
