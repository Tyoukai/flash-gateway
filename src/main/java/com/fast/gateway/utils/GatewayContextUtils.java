package com.fast.gateway.utils;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;

public class GatewayContextUtils {

    public static String getParam(ServerWebExchange exchange, String key) {
        ServerHttpRequest request = exchange.getRequest();
        MultiValueMap<String, String> params =  request.getQueryParams();
        return params.getFirst(key);
    }
}
