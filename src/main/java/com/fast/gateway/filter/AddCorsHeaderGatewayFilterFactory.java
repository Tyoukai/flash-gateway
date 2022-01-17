package com.fast.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import static org.jboss.netty.handler.codec.rtsp.RtspHeaders.Values.MAX_AGE;
import static org.springframework.web.cors.CorsConfiguration.ALL;

/**
 * 跨域处理的相关配置
 */
@Component
public class AddCorsHeaderGatewayFilterFactory extends AbstractGatewayFilterFactory<AddCorsHeaderGatewayFilterFactory.Config> {

    public AddCorsHeaderGatewayFilterFactory() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();
            HttpHeaders headers = response.getHeaders();
            headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, config.getAllowOrigin());
            headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, config.getAllowMethod());
            headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, config.getAllowCredentials());
            headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, config.getAllowHeaders());
            headers.add(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, ALL);
            headers.add(HttpHeaders.ACCESS_CONTROL_MAX_AGE, MAX_AGE);
            if (request.getMethod() == HttpMethod.OPTIONS) {
                response.setStatusCode(HttpStatus.OK);
                return Mono.empty();
            }
            return chain.filter(exchange);
        };
    }

    /**
     * see https://developer.mozilla.org/zh-CN/docs/Web/HTTP/CORS
     */
    public static class Config {
        private String allowOrigin;  // *

        private String allowMethod;  // "POST, GET, PUT, OPTIONS, DELETE, PATCH"

        private String allowCredentials; // true

        private String allowHeaders; // *

        public String getAllowOrigin() {
            return allowOrigin;
        }

        public void setAllowOrigin(String allowOrigin) {
            this.allowOrigin = allowOrigin;
        }

        public String getAllowMethod() {
            return allowMethod;
        }

        public void setAllowMethod(String allowMethod) {
            this.allowMethod = allowMethod;
        }

        public String getAllowCredentials() {
            return allowCredentials;
        }

        public void setAllowCredentials(String allowCredentials) {
            this.allowCredentials = allowCredentials;
        }

        public String getAllowHeaders() {
            return allowHeaders;
        }

        public void setAllowHeaders(String allowHeaders) {
            this.allowHeaders = allowHeaders;
        }
    }

}
