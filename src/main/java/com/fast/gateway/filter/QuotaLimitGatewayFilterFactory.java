package com.fast.gateway.filter;

import com.fast.gateway.common.QuotaLimitHelper;
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

import static com.fast.gateway.utils.Constants.*;

/**
 * 网关限额过滤器
 */
@Component
public class QuotaLimitGatewayFilterFactory extends AbstractGatewayFilterFactory<QuotaLimitGatewayFilterFactory.Config> {

    private static ScheduledExecutorService executorService = Executors.newScheduledThreadPool(3);

    @Autowired
    private QuotaLimitHelper quotaLimitHelper;

    public QuotaLimitGatewayFilterFactory() {
        super(Config.class);
    }


    @PostConstruct
    public void init() {

        // 1、初始化远程同步任务
        executorService.scheduleAtFixedRate(() -> quotaLimitHelper.pullRemoteToLocalAndPushLocalToRemote(),
                2, 1, TimeUnit.SECONDS);

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
                String prefixKey = GatewayContextUtils.buildKey(exchange, key);
                if (prefixKey.contains(UNKNOW)) {
                    continue;
                }
                if (!quotaLimitHelper.tryAcquire(prefixKey, 1)) {
                    return RewriteResponseUtils.rewriteResponse(exchange, "当前api超过额度限制");
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
    }
}
