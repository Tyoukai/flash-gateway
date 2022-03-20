package com.fast.gateway.repository;

import com.fast.gateway.entity.ApiRateLimitDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ApiRateLimitRepository {

    /**
     * 查询所有限流相关配置
     *
     * @return
     */
    List<ApiRateLimitDO> listAllApiRateLimitConfig();

    /**
     * 新增限流配置
     *
     * @param apiRateLimitDO
     * @return
     */
    int addApiRateLimitConfig(ApiRateLimitDO apiRateLimitDO);

    /**
     * 修改限流配置
     *
     * @param apiRateLimitDO
     * @return
     */
    int updateApiRateLimitConfig(ApiRateLimitDO apiRateLimitDO);

    /**
     * 删除限流配置
     *
     * @param apiRateLimitDO
     * @return
     */
    int deleteApiRateLimitConfig(ApiRateLimitDO apiRateLimitDO);
}
