package com.fast.gateway.filter.global;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.NettyWriteResponseFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import sun.misc.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

//@Component
public class TestFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange,
                             GatewayFilterChain chain) {
        System.out.println("TestFilter进入此方法");
        //构建响应拦截处理器
        BodyHandlerFunction bodyHandler = (resp, body) -> Flux.from(body)
                .map(dataBuffer -> {
                    //响应信息转换为字符串
                    String reqBody = "cost 123";
//                    try {
////                        dataBuffer 转换为String
//                        BufferedReader reader = new BufferedReader(new InputStreamReader(dataBuffer.asInputStream()));
//                        StringBuffer sb = new StringBuffer();
//                        String s;
//                        while ((s = reader.readLine()) != null) {
//                            sb.append(s);
//                        }
////                        reqBody = IOUtils
////                                .toString(dataBuffer.asInputStream(), "UTF-8")
////                                .replaceAll(">\\s{1,}<", "><");
//
//                        System.out.println(sb.toString());
//                        reqBody = sb.toString();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
                    return reqBody;
                }).flatMap(orgBody -> {
                    //根据原有的响应信息构建新响应信息并写入到resp中
                    //此处可以根据业务进行组装新的响应信息，
                    // 例如：登录后创建会话
                    //- 拿到登录请求的响应信息，判断登录是否成功
                    //- 登录成功调用创建会话接口，根据创建会话接口返回组装登录的真实响应
                    System.out.println("orgBody=" + orgBody);
                    String rbody = orgBody + "new1234";
                    HttpHeaders headers = resp.getHeaders();
                    headers.setContentLength(rbody.length());
                    return resp.writeWith(Flux.just(rbody)
                            .map(bx -> resp.bufferFactory()
                                    .wrap(bx.getBytes())));
                }).then();

        //构建响应包装类
        BodyHandlerServerHttpResponseDecorator responseDecorator = new BodyHandlerServerHttpResponseDecorator(
                bodyHandler, exchange.getResponse());
        return chain
                .filter(exchange.mutate().response(responseDecorator).build());
    }

    @Override
    public int getOrder() {
        //WRITE_RESPONSE_FILTER 之前执行
        return NettyWriteResponseFilter.WRITE_RESPONSE_FILTER_ORDER - 1;
    }

}

