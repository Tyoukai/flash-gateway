package com.fast.gateway.common;

/**
 * 全局过滤器的执行顺序
 */
public enum GlobalFilterOrderEnum {
    RESPONSE_WRITE(0, "response回写"),
    PARAMETER_MAPPING(1, "参数映射"),
    GRPC_ROUTE(2, "grpc路由"),

    ;

    GlobalFilterOrderEnum(int order, String desc) {
        this.order = order;
        this.desc = desc;
    }
    private int order;
    private String desc;

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
