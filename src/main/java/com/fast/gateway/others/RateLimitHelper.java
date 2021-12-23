package com.fast.gateway.others;

import com.fast.gateway.entity.ApiRateLimitDO;
import com.fast.gateway.service.ApiRateLimitService;
import com.google.common.util.concurrent.RateLimiter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.fast.gateway.utils.Constants.SPLIT_UNDERLINE;

@Component
public class RateLimitHelper {

    @Autowired
    private ApiRateLimitService apiRateLimitService;

    private CuratorFramework curatorClient;

    private static String zookeeperAddress = "42.192.49.234:2181";

    private static final String zkPath = "/flash_gateway/The-Flash";


    @PostConstruct
    public void init() {
        curatorClient = CuratorFrameworkFactory.builder()
                .connectString(zookeeperAddress)
                .connectionTimeoutMs(2000)
                .sessionTimeoutMs(10000)
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .build();

        curatorClient.start();
    }

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
                RateLimiter rateLimiter = rateLimiterMap.computeIfAbsent(buildKey(apiRateLimitDO.getApi(), apiRateLimitDO.getRateKey()),
                        key -> RateLimiter.create(apiRateLimitDO.getQps() / countClusterNode()));

                rateLimiter.setRate(apiRateLimitDO.getQps() / countClusterNode());
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
        try {
            List<String> children = curatorClient.getChildren().forPath(zkPath);
            if (CollectionUtils.isEmpty(children)) {
                return 1;
            }

            return children.size();
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        }
    }

    private String buildKey(String apiId, String rateKey) {
        return apiId + SPLIT_UNDERLINE + rateKey;
    }

}
