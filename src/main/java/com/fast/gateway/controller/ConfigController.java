package com.fast.gateway.controller;

import com.fast.gateway.entity.ApiQuotaLimitDO;
import com.fast.gateway.entity.ApiRateLimitDO;
import com.fast.gateway.service.ApiQuotaLimitService;
import com.fast.gateway.service.ApiRateLimitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/config")
public class ConfigController {

    @Autowired
    private ApiRateLimitService apiRateLimitService;

    @Autowired
    private ApiQuotaLimitService apiQuotaLimitService;

    @RequestMapping("/list-rate-limit")
    public Mono<List<ApiRateLimitDO>> listRateLimitConfig() {
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

    @RequestMapping("/list-quota-limit")
    public Mono<List<ApiQuotaLimitDO>> listQuotaLimitConfig() {
        return Mono.just(apiQuotaLimitService.listQuotaLimitConfig());
    }

    @RequestMapping("/add-update-quota-limit")
    public Mono<Boolean> addOrUpdateQuotaLimitConfig(Integer id,
                                                    @RequestParam String api, @RequestParam String quotaKey, @RequestParam Integer quota, @RequestParam Integer timeSpan) {
        if (id == null) {
            return Mono.just(apiQuotaLimitService.addQuotaLimitConfig(api, quotaKey, quota, timeSpan));
        } else {
            return Mono.just(apiQuotaLimitService.updateQuotaLimitConfig(id, api, quotaKey, quota, timeSpan));
        }
    }

    @RequestMapping("/delete-quota-limit")
    public Mono<Boolean> deleteQuotaLimitConfig(@RequestParam Integer id, @RequestParam String api) {
        return Mono.just(apiQuotaLimitService.deleteQuotaLimitConfig(id, api));
    }
}
