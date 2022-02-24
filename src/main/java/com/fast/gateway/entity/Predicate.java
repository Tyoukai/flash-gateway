package com.fast.gateway.entity;

import lombok.Data;

import java.util.Map;

@Data
public class Predicate {
    private String name;
    private Map<String, String> args;
}
