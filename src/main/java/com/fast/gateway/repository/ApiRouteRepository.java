package com.fast.gateway.repository;

import com.fast.gateway.entity.ApiRouteConfigDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;@Mapper
public interface ApiRouteRepository {

    List<ApiRouteConfigDO> listApiRouteConfig();
}
