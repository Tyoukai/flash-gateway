package com.fast.gateway.others;

import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class QuotaLimitHelper {
    private static volatile QuotaLimitHelper instance;

    private Map<String, QuotaLimitItem> quotaMap = new ConcurrentHashMap<>();

    private QuotaLimitHelper() {}

    public static QuotaLimitHelper getInstance() {
        if (instance == null) {
            synchronized (QuotaLimitHelper.class) {
                if (instance == null) {
                    instance = new QuotaLimitHelper();
                }
            }
        }
        return instance;
    }

    public boolean tryAcquire(String key, int delta) {
        long now = System.currentTimeMillis();
        QuotaLimitItem item = quotaMap.computeIfAbsent(key, k -> new QuotaLimitItem(k, 0, 0, -1, 0));

        // 刚初始化的限额，直接加额度然后返回
        if (item.getTotalQuota() < 0 || item.getExpireTime() <= 0) {
            item.addAndGet(delta);
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
    public void pullRemoteToLocalAndPushLocalToRemote(RedissonClient redisClient) {
        quotaMap.values().forEach(item -> {
            int localCount = item.getLocalCount().get();
            int localDelta = localCount - item.getLatestRemoteCount();

            RMap<Object, Object> quotaMap = redisClient.getMap(item.getKey());
//            quotaMap.addAndGet()
            // todo :接下来完成



        });
    }

    /**
     * 删除本地过期的限额配置
     */
    public void removeLocalExpiredQuotaConfig() {
        // 初始化的变量不删除，过期时间大于当前时间的不删除
        quotaMap.values().removeIf(item -> (item.getExpireTime() > 0 && item.getExpireTime() < System.currentTimeMillis()));
    }





}
