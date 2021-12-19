package com.fast.gateway.repository;

import com.fast.gateway.entity.ApiQuotaLimitDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ApiQuotaLimitRepository {

    /**
     * 查询所有限额相关配置
     *
     * @return
     */
    List<ApiQuotaLimitDO> listAllApiQuotaLimitConfig();

}
