package com.fast.gateway.controller;

import com.dianping.cat.Cat;
import com.fast.gateway.entity.ApiQuotaLimitDO;
import com.fast.gateway.entity.ApiRateLimitDO;
import com.fast.gateway.service.ApiQuotaLimitService;
import com.fast.gateway.service.ApiRateLimitService;
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

    @Autowired
    private ApiRateLimitService apiRateLimitService;

    @RequestMapping("/quota/config")
    public Mono<Map<String, ApiQuotaLimitDO>> listQuotaConfig() {
        return Mono.just(apiQuotaLimitService.listAllQuotaLimitConfig());
    }

    @RequestMapping("/rate/config")
    public Mono<Map<String, ApiRateLimitDO>> mapRateLimitConfig() {
        return Mono.just(apiRateLimitService.mapApiRateLimitConfig());
    }

    @RequestMapping("/rate/test")
    public Mono<String> rateLimitTestApi() {
        return Mono.just("rate.test");
    }

    @RequestMapping("/quota/test")
    public Mono<String> quotaLimitTestApi() {
        return Mono.just("quota.test");
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
