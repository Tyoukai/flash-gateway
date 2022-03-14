package com.fast.gateway.controller;

import com.fast.gateway.entity.ApiRateLimitDO;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/config")
public class ConfigController {


    @RequestMapping("/rate-limit")
    public Mono<List<ApiRateLimitDO>> mapRateLimitConfig() {
        ApiRateLimitDO apiRateLimitDO = new ApiRateLimitDO();
        apiRateLimitDO.setId(1);
        apiRateLimitDO.setApi("test.app.api");
        apiRateLimitDO.setRateKey("rate:key");
        apiRateLimitDO.setQps(1);

        ApiRateLimitDO apiRateLimitDO1 = new ApiRateLimitDO();
        apiRateLimitDO1.setId(2);
        apiRateLimitDO1.setApi("test.app.api2");
        apiRateLimitDO1.setRateKey("rate:key2");
        apiRateLimitDO1.setQps(2);
        List<ApiRateLimitDO> limitDOS = new ArrayList<>();
        limitDOS.add(apiRateLimitDO);
        limitDOS.add(apiRateLimitDO1);
        return Mono.just(limitDOS);
    }
}
