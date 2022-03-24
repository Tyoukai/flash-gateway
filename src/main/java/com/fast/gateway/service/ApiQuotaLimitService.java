package com.fast.gateway.service;

import com.fast.gateway.entity.ApiQuotaLimitDO;
import com.fast.gateway.repository.ApiQuotaLimitRepository;
import com.sun.org.apache.xpath.internal.operations.Bool;
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

    public Map<String, ApiQuotaLimitDO> listAllQuotaLimitConfig() {
        return ListUtils.emptyIfNull(apiQuotaLimitRepository.listAllApiQuotaLimitConfig()).stream()
                .filter(apiQuotaLimitDO -> (apiQuotaLimitDO.getQuota() > 0 && apiQuotaLimitDO.getTimeSpan() > 0))
                .collect(Collectors.toMap(this::buildQuotaLimitKey, Function.identity()));
    }

    public List<ApiQuotaLimitDO> listQuotaLimitConfig() {
        return apiQuotaLimitRepository.listAllApiQuotaLimitConfig();
    }

    public Boolean addQuotaLimitConfig(String api, String quotaKey, int quota, int timeSpan) {
        ApiQuotaLimitDO apiQuotaLimitDO = new ApiQuotaLimitDO();
        apiQuotaLimitDO.setApi(api);
        apiQuotaLimitDO.setQuotaKey(quotaKey);
        apiQuotaLimitDO.setQuota(quota);
        apiQuotaLimitDO.setTimeSpan(timeSpan);
        apiQuotaLimitDO.setCreateTime(System.currentTimeMillis());
        apiQuotaLimitDO.setUpdateTime(System.currentTimeMillis());
        return apiQuotaLimitRepository.addApiQuotaLimitConfig(apiQuotaLimitDO) > 0;
    }

    public Boolean updateQuotaLimitConfig(long id, String api, String quotaKey, int quota, int timeSpan) {
        ApiQuotaLimitDO apiQuotaLimitDO = new ApiQuotaLimitDO();
        apiQuotaLimitDO.setId(id);
        apiQuotaLimitDO.setApi(api);
        apiQuotaLimitDO.setQuotaKey(quotaKey);
        apiQuotaLimitDO.setQuota(quota);
        apiQuotaLimitDO.setTimeSpan(timeSpan);
        apiQuotaLimitDO.setUpdateTime(System.currentTimeMillis());
        return apiQuotaLimitRepository.updateApiQuotaLimitConfig(apiQuotaLimitDO) > 0;
    }

    public Boolean deleteQuotaLimitConfig(long id, String api) {
        ApiQuotaLimitDO apiQuotaLimitDO = new ApiQuotaLimitDO();
        apiQuotaLimitDO.setId(id);
        apiQuotaLimitDO.setApi(api);
        return apiQuotaLimitRepository.deleteApiQuotaLimitConfig(apiQuotaLimitDO) > 0;
    }

    private String buildQuotaLimitKey(ApiQuotaLimitDO apiQuotaLimitDO) {
        return apiQuotaLimitDO.getApi() +SPLIT_UNDERLINE + apiQuotaLimitDO.getQuotaKey();
    }


}
