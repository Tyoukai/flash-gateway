package com.fast.gateway.repository;

import com.fast.gateway.entity.CorsHeaderDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CorsHeaderRepository {
    /**
     * 获取所有的跨域配置
     *
     * @return
     */
    List<CorsHeaderDO> listCorsHeaderConfig();
}
