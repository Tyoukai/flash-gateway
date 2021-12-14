package com.fast.gateway.utils;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;

import static com.fast.gateway.utils.Constants.UNKNOW;

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
}
