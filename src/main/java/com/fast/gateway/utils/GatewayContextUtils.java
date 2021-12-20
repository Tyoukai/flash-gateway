package com.fast.gateway.utils;

import com.google.common.base.Strings;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;

import java.util.Arrays;
import java.util.stream.Collectors;

import static com.fast.gateway.utils.Constants.*;
import static com.fast.gateway.utils.Constants.SPLIT_SEMICOLON;
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR;

public class GatewayContextUtils {

    public static String getParam(ServerWebExchange exchange, String key) {
        ServerHttpRequest request = exchange.getRequest();
        MultiValueMap<String, String> params =  request.getQueryParams();
        String value = params.getFirst(key);
        if (StringUtils.isEmpty(value)) {
            return UNKNOW;
        }

        return value;
    }

    /**
     *
     * 构建限流和限额的key，规则：apiId_key1:value1;key2:value2
     */
    public static String buildKey(ServerWebExchange exchange, String key) {
        Route route = (Route) exchange.getAttributes().get(GATEWAY_ROUTE_ATTR);
        String id = route.getId();
        String[] keyArray = Strings.nullToEmpty(key).split(SPLIT_COMMA);
        return id + SPLIT_UNDERLINE + Arrays.stream(keyArray)
                .map(k -> k.trim() + SPLIT_COLON + getParam(exchange, k))
                .sorted(String::compareTo)
                .collect(Collectors.joining(SPLIT_SEMICOLON));
    }
}
