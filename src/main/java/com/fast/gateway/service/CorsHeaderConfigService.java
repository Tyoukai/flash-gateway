package com.fast.gateway.service;

import com.fast.gateway.entity.CorsHeaderDO;
import com.fast.gateway.repository.CorsHeaderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CorsHeaderConfigService {

    @Autowired
    private CorsHeaderRepository corsHeaderRepository;

    public List<CorsHeaderDO> listCorsHeaderConfig() {
        return corsHeaderRepository.listCorsHeaderConfig();
    }
}
