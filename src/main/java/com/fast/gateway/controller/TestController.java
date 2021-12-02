package com.fast.gateway.controller;

import com.dianping.cat.Cat;
import com.fast.gateway.entity.ApiQuotaLimit;
import com.fast.gateway.service.ApiQuotaLimitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private ApiQuotaLimitService apiQuotaLimitService;

    @RequestMapping("/quota/config")
    public Mono<Map<String, ApiQuotaLimit>> getQuotaConfig() {
        return Mono.just(apiQuotaLimitService.getAllQuotaLimitConfig());
    }

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
