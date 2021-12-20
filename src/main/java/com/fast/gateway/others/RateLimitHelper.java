package com.fast.gateway.others;

import com.fast.gateway.entity.ApiRateLimitDO;
import com.fast.gateway.service.ApiRateLimitService;
import com.google.common.util.concurrent.RateLimiter;
import org.apache.commons.collections4.ListUtils;
import org.apache.curator.framework.CuratorFramework;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.fast.gateway.utils.Constants.SPLIT_UNDERLINE;

@Component
public class RateLimitHelper {

    @Autowired
    private ApiRateLimitService apiRateLimitService;

    private CuratorFramework curatorClient;

    /**
     * 存放api不同维度的限流配置
     * key: apiId_key1:value1;key2:value2
     */
    private Map<String, RateLimiter> rateLimiterMap = new ConcurrentHashMap<>();

    public boolean tryAcquire(String key) {
        RateLimiter rateLimiter = rateLimiterMap.get(key);
        if (rateLimiter == null) {
            return true;
        }

        return rateLimiter.tryAcquire();
    }

    public void syncQpsInDataBase() {
        try {
            List<ApiRateLimitDO> apiRateLimitDOS = apiRateLimitService.listAllApiRateLimitConfig();
            ListUtils.emptyIfNull(apiRateLimitDOS).forEach(apiRateLimitDO -> {
                rateLimiterMap.computeIfAbsent(buildKey(apiRateLimitDO.getApi(), apiRateLimitDO.getRateKey()),
                        key -> RateLimiter.create(apiRateLimitDO.getQps() / countClusterNode()));
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取集群中节点的数量
     *
     * @return
     */
    private double countClusterNode() {
        // todo:
        return 1;
    }

    private String buildKey(String apiId, String rateKey) {
        return apiId + SPLIT_UNDERLINE + rateKey;
    }

}
