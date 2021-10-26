package com.fast.gateway.filter.global;

import com.fast.gateway.others.GlobalFilterOrderEnum;
import com.fast.gateway.utils.ObjectMapperUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

import static com.fast.gateway.utils.Constants.*;


/**
 * 参数映射的Filter
 * 在http->grpc或grpc->http时进行参数映射
 */
@Component
public class ParameterMappingFilter implements GlobalFilter, Ordered {
    private Map<String, String> inputMap = new HashMap<>();
    private Map<String, String> outputMap = new HashMap<>();

    @PostConstruct
    public void init() {
        inputMap.put("amount", "amount");
        inputMap.put("fromUserId", "from_user_id");
        inputMap.put("toUserId", "to_user_id");
        inputMap.put("timestamp", "timestamp");

        outputMap.put("code", "code");
        outputMap.put("msg", "msg");
    }

    /**
     * 从request中获取param参数，然后进行参数映射
     *
     * @param exchange
     * @param chain
     * @return
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        MultiValueMap<String, String> map = exchange.getRequest().getQueryParams();
        String param = map.getFirst(PARAM);
        if (param == null) {
            return chain.filter(exchange);
        }
        String covertStr = covertToJson(map.getFirst(PARAM), 1);
        exchange.getAttributes().put(INPUT_JSON_STRING, covertStr);

        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            String exeStr = (String) exchange.getAttributes().get(GRPC_EXE_JSON_STRING);
            String returnStr = covertToJson(exeStr, 2);
            exchange.getAttributes().put(OUTPUT_JSON_STRING, returnStr);
        }));
    }

    private String covertToJson(String param, int type) {
        Map<String, String> originMap = ObjectMapperUtils.fromJson(param, Map.class);
        Map<String, String> mapping = new HashMap<>();

        for(Map.Entry<String, String> entry : originMap.entrySet()) {
            if (type == 1) {
                mapping.put(inputMap.get(entry.getKey()), entry.getValue());
            } else if (type == 2) {
                mapping.put(outputMap.get(entry.getKey()), entry.getValue());
            }

        }
        return ObjectMapperUtils.toJson(mapping);
    }

    @Override
    public int getOrder() {
        return GlobalFilterOrderEnum.PARAMETER_MAPPING.getOrder();
    }
}
