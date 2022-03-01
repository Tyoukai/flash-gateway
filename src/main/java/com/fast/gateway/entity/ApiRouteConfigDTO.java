package com.fast.gateway.entity;

import lombok.Data;

import java.util.List;

/**
 * 对应真实的字段
 */
@Data
public class ApiRouteConfigDTO {
    private String id;
    private String uri;
    private List<Predicate> predicates;
    private List<Filter> filters;
    private int order;
}
