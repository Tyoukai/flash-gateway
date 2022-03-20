package com.fast.gateway.controller;

import com.fast.gateway.entity.ApiRateLimitDO;
import com.fast.gateway.service.ApiRateLimitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/config")
public class ConfigController {

    @Autowired
    private ApiRateLimitService apiRateLimitService;

    @RequestMapping("/list-rate-limit")
    public Mono<List<ApiRateLimitDO>> mapRateLimitConfig() {
        return Mono.just(apiRateLimitService.listAllApiRateLimitConfig());
    }

    @RequestMapping("/add-update-rate-limit")
    public Mono<Boolean> addOrUpdateRateLimitConfig(Integer id,
                                                    @RequestParam String api, @RequestParam String rateKey, @RequestParam Integer qps) {
        if (id == null) {
            return Mono.just(apiRateLimitService.addApiRateLimitConfig(api, rateKey, qps));
        } else {
            return Mono.just(apiRateLimitService.updateRateLimitConfig(id, api, rateKey, qps));
        }
    }

    @RequestMapping("/delete-rate-limit")
    public Mono<Boolean> deleteRateLimitConfig(@RequestParam Integer id, @RequestParam String api) {
        return Mono.just((apiRateLimitService.deleteRateLimitConfig(id, api)));
    }
}
