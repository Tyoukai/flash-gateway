package com.fast.gateway.service;

import com.fast.gateway.common.DataConvert;
import com.fast.gateway.entity.ApiRouteConfigDO;
import com.fast.gateway.entity.ApiRouteConfigDTO;
import com.fast.gateway.repository.ApiRouteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApiRouteService {

    @Autowired
    private ApiRouteRepository apiRouteRepository;

    public List<ApiRouteConfigDTO> listApiRouteConfig() {
        List<ApiRouteConfigDO> apiRouteConfigDOS = apiRouteRepository.listApiRouteConfig();
        return DataConvert.toDto(apiRouteConfigDOS);
    }
}
