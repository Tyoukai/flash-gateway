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

    /**
     * 新增
     *
     * @return
     */
    int addApiQuotaLimitConfig(ApiQuotaLimitDO apiQuotaLimitDO);

    /**
     * 修改
     *
     * @return
     */
    int updateApiQuotaLimitConfig(ApiQuotaLimitDO apiQuotaLimitDO);

    /**
     * 删除
     *
     * @return
     */
    int deleteApiQuotaLimitConfig(ApiQuotaLimitDO apiQuotaLimitDO);

}
