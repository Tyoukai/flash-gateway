package com.fast.gateway.filter;

import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 限流Filter
 */
@Component
public class RateLimitGatewayFilterFactory extends AbstractGatewayFilterFactory<RateLimitGatewayFilterFactory.Config> {

    public RateLimitGatewayFilterFactory() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        System.out.println("config:" + config);
        return new RateLimitGatewayFilter(config);
    }

    private class RateLimitGatewayFilter implements GatewayFilter {
        private Config config;

        public RateLimitGatewayFilter(Config config) {
            this.config = config;
        }

        /**
         * 根据appkey 获取当前的访问次数，再根据设置的值判断是否允许访问
         *
         * @param exchange
         * @param chain
         * @return
         */
        @Override
        public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
            System.out.println("RateLimitGatewayFilterFactory execute");
            String appKeyInRequest = exchange.getAttribute("appKey");
            String appKey = config.getAppKey();

            if (StringUtils.equals(appKeyInRequest, appKey) && getCount(appKey) > config.getQps()) {
                return Mono.error(new Exception("超过限流值"));
            }

            return chain.filter(exchange);
        }

        private long getCount(String appKey) {
            return 10;
        }
    }

    public static class Config {
        private String appKey;
        private int qps;

        public String getAppKey() {
            return appKey;
        }

        public void setAppKey(String appkey) {
            this.appKey = appkey;
        }

        public int getQps() {
            return qps;
        }

        public void setQps(int qps) {
            this.qps = qps;
        }

        @Override
        public String toString() {
            return super.toString();
        }
    }
}
