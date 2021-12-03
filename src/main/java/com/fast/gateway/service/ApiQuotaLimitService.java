package com.fast.gateway.service;

import com.fast.gateway.entity.ApiQuotaLimitDO;
import com.fast.gateway.repository.ApiQuotaLimitRepository;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.fast.gateway.utils.Constants.SPLIT_UNDERLINE;

@Service
public class ApiQuotaLimitService {

    @Autowired
    private ApiQuotaLimitRepository apiQuotaLimitRepository;

    public Map<String, ApiQuotaLimitDO> listAllQuotaLimitConfig() {
        return ListUtils.emptyIfNull(apiQuotaLimitRepository.listAllApiQuotaLimitConfig()).stream()
                .filter(apiQuotaLimitDO -> (apiQuotaLimitDO.getQuota() > 0 && apiQuotaLimitDO.getTimeSpan() > 0))
                .collect(Collectors.toMap(this::buildQuotaLimitKey, Function.identity()));
    }

    private String buildQuotaLimitKey(ApiQuotaLimitDO apiQuotaLimitDO) {
        return apiQuotaLimitDO.getApi() +SPLIT_UNDERLINE + apiQuotaLimitDO.getQuotaKey();
    }


}
