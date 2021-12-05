package com.fast.gateway.utils;

import io.netty.util.CharsetUtil;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class RewriteResponseUtils {

    public static Mono<Void> rewriteResponse(ServerWebExchange exchange, String content) {
        byte[] byteContent = content.getBytes(CharsetUtil.UTF_8);
        ServerHttpResponse response = exchange.getResponse();
        DataBuffer dataBuffer = response.bufferFactory().allocateBuffer(byteContent.length).write(byteContent);
        return response.writeWith(Flux.just(dataBuffer));
    }
}
