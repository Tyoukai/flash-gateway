package com.fast.gateway.controller;

import com.fast.gateway.entity.ApiRateLimitDO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
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
        List<ApiRateLimitDO> limitDOS = new ArrayList<>();
        limitDOS.add(apiRateLimitDO);
        return Mono.just(limitDOS);
    }
}
