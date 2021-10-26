package com.fast.gateway.filter.global;

import io.netty.util.CharsetUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.fast.gateway.others.GlobalFilterOrderEnum.RESPONSE_WRITE;
import static com.fast.gateway.utils.Constants.OUTPUT_JSON_STRING;


/**
 * response回写filter，在真正转发时，重写response
 */
@Component
public class ResponseWriteFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        return chain.filter(exchange).then(Mono.defer(() -> {
            String result = (String) exchange.getAttributes().get(OUTPUT_JSON_STRING);
            // http请求不重写直接原样返回
            if (StringUtils.isBlank(result)) {
                return Mono.empty();
            }
            byte[] byteResult = result.getBytes(CharsetUtil.UTF_8);
            ServerHttpResponse response = exchange.getResponse();
            DataBuffer dataBuffer = response.bufferFactory().allocateBuffer(byteResult.length).write(byteResult);
            return response.writeWith(Flux.just(dataBuffer));
        }));
    }

    @Override
    public int getOrder() {
        return RESPONSE_WRITE.getOrder();
    }
}
