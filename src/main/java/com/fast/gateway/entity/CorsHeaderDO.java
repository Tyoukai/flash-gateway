package com.fast.gateway.entity;

import lombok.Data;

@Data
public class CorsHeaderDO {
    private String allowOrigin;
    private String allowMethod;
    private String allowCredentials;
    private String allowHeaders;
}
