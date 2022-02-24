package com.fast.gateway.common;

import com.fast.gateway.entity.ApiQuotaLimitDO;
import com.fast.gateway.service.ApiQuotaLimitService;
import org.redisson.Redisson;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static com.fast.gateway.utils.Constants.SPLIT_UNDERLINE;

@Component
public class QuotaLimitHelper {

    private static RedissonClient redisClient;

    private static final int SECOND_TO_MILLIS = 1000;

    @Autowired
    private ApiQuotaLimitService apiQuotaLimitService;

    /**
     * 本地限额缓存
     *
     * key: prefixKey
     * value:
     *          key: prefixKey_tailKey
     *          value: 真实的缓存对象
     */
    private Map<String, ConcurrentHashMap<String, QuotaLimitItem>> localCacheQuotaMap = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        Config config = new Config();
        config.useReplicatedServers()
                .addNodeAddress("redis://101.43.59.148:6379")
                .setPassword("hust123456");
        redisClient = Redisson.create(config);
    }

    public boolean tryAcquire(String key, int delta) {
        Map<String, QuotaLimitItem> quotaLimitItemMap = localCacheQuotaMap.get(key);
        // 未初始化过，表示没有配置限额，直接放行
        if (quotaLimitItemMap == null || quotaLimitItemMap.size() <= 0) {
            return true;
        }

        String firstKey = quotaLimitItemMap.keySet().stream().findFirst().get();
        long timeSpan = quotaLimitItemMap.get(firstKey).getTimeSpan();

        long now = System.currentTimeMillis();
        QuotaLimitItem item = quotaLimitItemMap.get(key + SPLIT_UNDERLINE + TimeUnit.MILLISECONDS.toSeconds(now) / timeSpan);
        // 当前对应的item还未创建
        if (item == null) {
            return true;
        }

        if (now < item.getExpireTime()) {
            int latestLocalCount  = item.addAndGet(delta);
            // 当前最新使用额度是否小于等于总额度
            return latestLocalCount <= item.getTotalQuota();
        }

        return false;
    }

    /**
     * 同步远程限额情况到本地，同时将本地限额情况同步到远程
     */
    public void pullRemoteToLocalAndPushLocalToRemote() {
        try {
            localCacheQuotaMap.values().forEach(itemMap -> {
                itemMap.values().forEach(item -> {
                    int localCount = item.getLocalCount().get();
                    // 1、本机新增的调用次数
                    int localDelta = localCount - item.getLatestRemoteCount();
                    RMap<Object, Object> quotaInRedis = redisClient.getMap(item.buildKey(), new StringCodec("utf-8"));
                    // 2、将本机新增次数同步到远程
                    quotaInRedis.addAndGet("usedQuota", localDelta);
                    // 3、获取远程最新使用的额度数
                    int remoteCount = Integer.parseInt(quotaInRedis.get("usedQuota").toString());
                    // 4、计算出此刻其他机器调用的额度数
                    int remoteDelta = remoteCount - localCount;
                    // 5、将本地远程值更新为远端的最新值
                    item.setLatestRemoteCount(remoteCount);
                    // 6、将本地已经调用次数加上其他机器使用的额度
                    item.getLocalCount().addAndGet(remoteDelta);

                });
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除本地过期的限额配置
     */
    public void removeLocalExpiredQuotaConfig() {
        // 初始化的变量不删除，过期时间大于当前时间的不删除
        try {
            localCacheQuotaMap.values().forEach(realMap ->
                    realMap.values().removeIf(item -> (item.getExpireTime() > 0 && item.getExpireTime() < System.currentTimeMillis())));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 同步本地数据库中不同维度key的过期时间和总的额度
     */
    public void syncExpireTimeAndTotalQuota() {
        try {
            Map<String, ApiQuotaLimitDO> apiQuotaLimitDOMap = apiQuotaLimitService.listAllQuotaLimitConfig();
            apiQuotaLimitDOMap.forEach((k, v) -> {
                // 1、更新本地缓存的过期时间和总额度
                long now = System.currentTimeMillis();
                long tailKey = TimeUnit.MILLISECONDS.toSeconds(now) / v.getTimeSpan();
                ConcurrentHashMap<String, QuotaLimitItem> quotaLimitItemMap = localCacheQuotaMap.get(k);
                if (quotaLimitItemMap == null) {
                    quotaLimitItemMap = new ConcurrentHashMap<>();
                    localCacheQuotaMap.put(k, quotaLimitItemMap);
                }

                String localKey = k + SPLIT_UNDERLINE + tailKey;
                QuotaLimitItem item = quotaLimitItemMap.computeIfAbsent(localKey,
                        key -> new QuotaLimitItem(k,  tailKey, 0, 0, v.getQuota(), v.getTimeSpan(), -1, now));
                item.setExpireTime(item.getStartTime() + v.getTimeSpan() * SECOND_TO_MILLIS);
                item.setTotalQuota(v.getQuota());

                // 2、更新redis中的相关数据
                RMap<Object, Object> quotaInRedis = redisClient.getMap(item.buildKey(), new StringCodec("utf-8"));
                quotaInRedis.put("totalQuota", item.getTotalQuota());
                quotaInRedis.put("expireTime", item.getExpireTime());
                quotaInRedis.put("startTime", item.getStartTime());
                quotaInRedis.expireAt(item.getExpireTime());
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
