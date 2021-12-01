package com.fast.gateway.entity;

import lombok.Data;

@Data
public class ApiQuotaLimit {
    private long id;
    private String api;
    private String quotaKey;
    private int quota;
    private int timeSpan;
    private long createTime;
    private long updateTime;
}
