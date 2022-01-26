package com.fast.gateway.entity;

import lombok.Data;

/**
 * 参数映射DO
 */
@Data
public class ParameterMappingDO {
    private long id;
    private String api;
    private int type;
    private String from;
    private String to;
    private long createTime;
    private long updateTime;
}
