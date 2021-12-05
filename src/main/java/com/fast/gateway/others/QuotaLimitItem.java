package com.fast.gateway.others;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 本地计数的对象封装
 */
public class QuotaLimitItem {
    // 对应的限流key
    private String key;
    // 本地已经计数的次数
    private AtomicInteger localCount;
    // 最新远程计数次数
    private int latestRemoteCount;
    // 总额度
    private int totalQuota;
    // 过期的时间
    private long expireTime;

    public QuotaLimitItem(String key, int localCount, int latestRemoteCount, int totalQuota, long expireTime) {
        this.key = key;
        this.localCount = new AtomicInteger(localCount);
        this.latestRemoteCount = latestRemoteCount;
        this.totalQuota = totalQuota;
        this.expireTime = expireTime;
    }

    public int addAndGet(int delta) {
        return localCount.addAndGet(delta);
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public AtomicInteger getLocalCount() {
        return localCount;
    }

    public void setLocalCount(AtomicInteger localCount) {
        this.localCount = localCount;
    }

    public int getLatestRemoteCount() {
        return latestRemoteCount;
    }

    public void setLatestRemoteCount(int latestRemoteCount) {
        this.latestRemoteCount = latestRemoteCount;
    }

    public int getTotalQuota() {
        return totalQuota;
    }

    public void setTotalQuota(int totalQuota) {
        this.totalQuota = totalQuota;
    }

    public long getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(long expireTime) {
        this.expireTime = expireTime;
    }
}
