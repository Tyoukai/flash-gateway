package com.fast.gateway.service;

import com.fast.gateway.entity.ApiRateLimitDO;
import com.fast.gateway.repository.ApiRateLimitRepository;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.fast.gateway.utils.Constants.SPLIT_UNDERLINE;

@Service
public class ApiRateLimitService {

    @Autowired
    private ApiRateLimitRepository apiRateLimitRepository;

    public Map<String, ApiRateLimitDO> mapApiRateLimitConfig() {
        List<ApiRateLimitDO> apiRateLimitDOList = apiRateLimitRepository.listAllApiRateLimitConfig();
        return ListUtils.emptyIfNull(apiRateLimitDOList).stream()
                .filter(apiRateLimitDO -> apiRateLimitDO.getQps() > 0)
                .collect(Collectors.toMap(this::buildApiLimitConfigKey, Function.identity()));
    }

    public List<ApiRateLimitDO> listAllApiRateLimitConfig() {
        return apiRateLimitRepository.listAllApiRateLimitConfig();
    }

    private String buildApiLimitConfigKey(ApiRateLimitDO apiRateLimitDO) {
        return apiRateLimitDO.getApi() + SPLIT_UNDERLINE + apiRateLimitDO.getRateKey();
    }
}
