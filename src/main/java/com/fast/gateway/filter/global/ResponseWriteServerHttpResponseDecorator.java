package com.fast.gateway.filter.global;

import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import static com.fast.gateway.utils.Constants.OUTPUT_JSON_STRING;


public class ResponseWriteServerHttpResponseDecorator extends ServerHttpResponseDecorator {

    private ServerWebExchange exchange;

    public ResponseWriteServerHttpResponseDecorator(ServerWebExchange exchange) {
        super(exchange.getResponse());
        this.exchange = exchange;
    }


    @Override
    public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
        return Flux.from(body)
                .map(dataBuffer -> {
                    //响应信息转换为字符串
                    String originResponse = null;
                    try {
//                        dataBuffer 转换为String
                        StringBuffer sb = new StringBuffer();
                        String s;
                        BufferedReader reader = new BufferedReader(new InputStreamReader(dataBuffer.asInputStream()));
                        while ((s = reader.readLine()) != null) {
                            sb.append(s);
                        }
                        System.out.println(sb.toString());
                        originResponse = sb.toString();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return originResponse;
                }).flatMap(originResponse -> {
                    String result = (String) exchange.getAttributes().get(OUTPUT_JSON_STRING);
                    System.out.println("result:" + result);

                    ServerHttpResponse response = exchange.getResponse();
                    response.getHeaders().setContentLength(result.length());
                    return response.writeWith(Flux.just(result)
                    .map(bx ->response.bufferFactory().wrap(bx.getBytes()) ));
                }).then();
    }

    @Override
    public Mono<Void> writeAndFlushWith(
            Publisher<? extends Publisher<? extends DataBuffer>> body) {
        return writeWith(Flux.from(body).flatMapSequential(p -> p));
    }

}
