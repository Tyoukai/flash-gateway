package com.fast.gateway.entity;

import lombok.Data;

import java.util.List;

@Data
public class ApiRouteConfigDTO {
    private long id;
    private String apiId;
    private String uri;
    private List<Predicate> predicates;
    private List<Filter> filters;
    private int order;
}
