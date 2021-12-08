package com.fast.gateway.filter;

import com.fast.gateway.others.QuotaLimitHelper;
import com.fast.gateway.utils.GatewayContextUtils;
import com.fast.gateway.utils.RewriteResponseUtils;
import com.google.common.base.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.fast.gateway.utils.Constants.*;
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR;

/**
 * 网关限额过滤器
 */
@Component
public class QuotaLimitGatewayFilterFactory extends AbstractGatewayFilterFactory<QuotaLimitGatewayFilterFactory.Config> {

    private static ScheduledExecutorService executorService = Executors.newScheduledThreadPool(10);

    @Autowired
    private QuotaLimitHelper quotaLimitHelper;

    public QuotaLimitGatewayFilterFactory() {
        super(Config.class);
    }


    @PostConstruct
    public void init() {

        // 1、初始化远程同步任务
        executorService.scheduleAtFixedRate(() -> quotaLimitHelper.pullRemoteToLocalAndPushLocalToRemote(),
                0, 1, TimeUnit.SECONDS);

        // 2、删除本地过期的限额配置
        executorService.scheduleAtFixedRate(() -> quotaLimitHelper.removeLocalExpiredQuotaConfig(),
                0, 2, TimeUnit.SECONDS);

        // 3、同步数据库中过期时间和总额度
        executorService.scheduleAtFixedRate(() -> quotaLimitHelper.syncExpireTimeAndTotalQuota(),
                0, 1, TimeUnit.SECONDS);
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

    /**
     * 规则：id_key1:value1;key2:value2
     *
     * @param key
     * @param exchange
     * @return
     */
    private String buildKey(String key, ServerWebExchange exchange) {
        Route route = (Route) exchange.getAttributes().get(GATEWAY_ROUTE_ATTR);
        String id = route.getId();
        String[] keyArray = Strings.nullToEmpty(key).split(SPLIT_COMMA);
        return id + SPLIT_UNDERLINE + Arrays.stream(keyArray)
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
