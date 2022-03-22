package com.fast.gateway.controller;

import com.fast.gateway.entity.ApiQuotaLimitDO;
import com.fast.gateway.entity.ApiRateLimitDO;
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
        List<ApiQuotaLimitDO> list = new ArrayList<>();
        ApiQuotaLimitDO apiQuotaLimitDO = new ApiQuotaLimitDO();
        apiQuotaLimitDO.setId(1);
        apiQuotaLimitDO.setApi("web.test.api");
        apiQuotaLimitDO.setQuotaKey("key:value");
        apiQuotaLimitDO.setQuota(10);
        apiQuotaLimitDO.setTimeSpan(1800);

        ApiQuotaLimitDO apiQuotaLimitDO2 = new ApiQuotaLimitDO();
        apiQuotaLimitDO2.setId(2);
        apiQuotaLimitDO2.setApi("web.test.api2");
        apiQuotaLimitDO2.setQuotaKey("key:value2");
        apiQuotaLimitDO2.setQuota(10);
        apiQuotaLimitDO2.setTimeSpan(1800);

        list.add(apiQuotaLimitDO);
        list.add(apiQuotaLimitDO2);

        return Mono.just(list);
    }
}
