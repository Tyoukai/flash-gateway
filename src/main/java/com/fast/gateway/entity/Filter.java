package com.fast.gateway.entity;

import lombok.Data;

import java.util.Map;

@Data
public class Filter {
    private String name;
    private Map<String, String> args;
}
