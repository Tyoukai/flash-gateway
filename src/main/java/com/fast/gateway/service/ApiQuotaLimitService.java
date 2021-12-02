package com.fast.gateway.service;

import com.fast.gateway.entity.ApiQuotaLimit;
import com.fast.gateway.repository.ApiQuotaLimitRepository;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.fast.gateway.utils.Constants.SPLIT_UNDERLINE;

@Service
public class ApiQuotaLimitService {

    @Autowired
    private ApiQuotaLimitRepository apiQuotaLimitRepository;

    public Map<String, ApiQuotaLimit> getAllQuotaLimitConfig() {
        return ListUtils.emptyIfNull(apiQuotaLimitRepository.getAllApiQuotaLimitConfig()).stream()
                .filter(apiQuotaLimit -> (apiQuotaLimit.getQuota() > 0 && apiQuotaLimit.getTimeSpan() > 0))
                .collect(Collectors.toMap(this::buildQuotaLimitKey, Function.identity()));
    }

    private String buildQuotaLimitKey(ApiQuotaLimit apiQuotaLimit) {
        return apiQuotaLimit.getApi() +SPLIT_UNDERLINE + apiQuotaLimit.getQuotaKey();
    }


}
