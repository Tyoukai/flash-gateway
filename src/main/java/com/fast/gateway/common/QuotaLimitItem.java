package com.fast.gateway.common;

import java.util.concurrent.atomic.AtomicInteger;

import static com.fast.gateway.utils.Constants.SPLIT_UNDERLINE;

/**
 * 本地计数的对象封装
 */
public class QuotaLimitItem {
    // 对应的限流key的前缀
    private String prefixKey;
    // 对应的限流key的尾缀
    private long tailKey;
    // 本地已经计数的次数
    private AtomicInteger localCount;
    // 最新远程计数次数
    private int latestRemoteCount;
    // 总额度
    private int totalQuota;
    // 时间跨度
    private int timeSpan;
    // 过期的时间戳
    private long expireTime;
    // 开始计数的时间戳
    private long startTime;

    public QuotaLimitItem(String prefixKey, long tailKey,  int localCount, int latestRemoteCount, int totalQuota, int timeSpan, long expireTime, long startTime) {
        this.prefixKey = prefixKey;
        this.tailKey = tailKey;
        this.localCount = new AtomicInteger(localCount);
        this.latestRemoteCount = latestRemoteCount;
        this.totalQuota = totalQuota;
        this.timeSpan = timeSpan;
        this.expireTime = expireTime;
        this.startTime = startTime;
    }

    public int addAndGet(int delta) {
        return localCount.addAndGet(delta);
    }

    public String buildKey() {
        return prefixKey + SPLIT_UNDERLINE + tailKey;
    }

    @Override
    public String toString() {
        return this.buildKey();
    }

    public String getPrefixKey() {
        return prefixKey;
    }

    public void setPrefixKey(String prefixKey) {
        this.prefixKey = prefixKey;
    }

    public long getTailKey() {
        return tailKey;
    }

    public void setTailKey(long tailKey) {
        this.tailKey = tailKey;
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

    public int getTimeSpan() {
        return timeSpan;
    }

    public void setTimeSpan(int timeSpan) {
        this.timeSpan = timeSpan;
    }

    public long getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(long expireTime) {
        this.expireTime = expireTime;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }
}
