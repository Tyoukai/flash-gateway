package com.fast.gateway.controller;

import com.dianping.cat.Cat;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/test")
public class TestController {

    @RequestMapping("/rate")
    public Mono<String> rateLimit() {
        Cat.logMetricForCount("rate.limit");
        return Mono.just("rate-limit");
    }

    @RequestMapping("/logRecord")
    public Mono<String> logRecord() {
        return Mono.just("log-record");
    }
}
