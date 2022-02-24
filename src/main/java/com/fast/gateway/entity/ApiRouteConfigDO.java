package com.fast.gateway.entity;

import lombok.Data;

@Data
public class ApiRouteConfigDO {
    private long id;
    private String apiId;
    private String uri;
    private String predicates;
    private String filters;
    private int order;
}
