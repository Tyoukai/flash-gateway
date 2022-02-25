package com.fast.gateway.common;

import com.fast.gateway.entity.ApiRouteConfigDO;
import com.fast.gateway.entity.ApiRouteConfigDTO;
import com.fast.gateway.entity.Filter;
import com.fast.gateway.entity.Predicate;
import com.fast.gateway.utils.ObjectMapperUtils;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

public class DataConvert {

    public static ApiRouteConfigDTO toDto(ApiRouteConfigDO apiRouteConfigDO) {
        ApiRouteConfigDTO apiRouteConfigDTO = new ApiRouteConfigDTO();
        apiRouteConfigDTO.setId(apiRouteConfigDO.getId());
        apiRouteConfigDTO.setApiId(apiRouteConfigDO.getApiId());
        apiRouteConfigDTO.setUri(apiRouteConfigDO.getUri());
        apiRouteConfigDTO.setPredicates(ObjectMapperUtils.fromJson(apiRouteConfigDO.getPredicates(), Predicate.class, List.class));
        apiRouteConfigDTO.setFilters(ObjectMapperUtils.fromJson(apiRouteConfigDO.getFilters(), Filter.class, List.class));
        apiRouteConfigDTO.setOrder(apiRouteConfigDO.getOrder());

        return apiRouteConfigDTO;
    }

    public static List<ApiRouteConfigDTO> toDto(List<ApiRouteConfigDO> apiRouteConfigDOS) {
        return CollectionUtils.emptyIfNull(apiRouteConfigDOS).stream()
                .map(apiRouteConfigDO -> toDto(apiRouteConfigDO))
                .collect(Collectors.toList());
    }
}
