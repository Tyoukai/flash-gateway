package com.fast.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;

/**
 * 网关限额过滤器
 */
//@Component
public class QuotaLimitGatewayFilterFactory extends AbstractGatewayFilterFactory<QuotaLimitGatewayFilterFactory.Config> {
    @Override
    public GatewayFilter apply(Config config) {
        return null;
    }

    public static class Config {
        private String appId;

    }
}
