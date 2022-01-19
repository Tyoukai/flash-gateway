package com.fast.gateway.filter;

import com.fast.gateway.utils.ObjectMapperUtils;
import com.google.common.collect.Lists;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import sun.misc.IOUtils;

import java.io.IOException;
import java.time.Duration;
import java.util.List;

/**
 * 日志相关的过滤器
 */
@Component
@Slf4j
public class LogRecordGatewayFilterFactory extends AbstractGatewayFilterFactory<LogRecordGatewayFilterFactory.Config> {

    public LogRecordGatewayFilterFactory() {
        super(Config.class);
    }

    private static String REQUESR_BEGIN_TIME = "requestBeginTime";
    private static String RESPONSE_COOKIE_NAME = "rt-record";
    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            exchange.getAttributes().put(REQUESR_BEGIN_TIME, System.currentTimeMillis());
            MultiValueMap<String, String> map = exchange.getRequest().getQueryParams();
            log.info("API_INPUT:{}", ObjectMapperUtils.toJson(map));

            return chain.filter(exchange).then(
                    Mono.defer(() -> {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            log.error("InterruptedException", e);
                        }
                        Long startTime = exchange.getAttribute(REQUESR_BEGIN_TIME);
                        String cost = String.valueOf(System.currentTimeMillis() - startTime);
                        log.info("cost:{}", cost);
                        String responseCost = "cost: " + cost;
                        byte[] byteResponseCost = responseCost.getBytes(CharsetUtil.UTF_8);
                        ServerHttpResponse response = exchange.getResponse();
                        response.setStatusCode(HttpStatus.OK);
                        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_HTML_VALUE);
                        DataBuffer buffer = response.bufferFactory().allocateBuffer(byteResponseCost.length).write(byteResponseCost);
                        return response.writeWith(Flux.just(buffer));
                    })
            );
        };
    }

    public static class Config {
        private String appKey;

        public String getAppKey() {
            return appKey;
        }

        public void setAppKey(String appKey) {
            this.appKey = appKey;
        }
    }
}
