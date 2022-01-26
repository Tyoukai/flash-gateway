package com.fast.gateway.entity;

import lombok.Data;

@Data
public class ApiRateLimitDO {
    private long id;
    private String api;
    private String rateKey;
    private int qps;
    private long createTime;
    private long updateTime;
}
