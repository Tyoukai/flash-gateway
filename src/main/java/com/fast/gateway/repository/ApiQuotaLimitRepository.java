package com.fast.gateway.repository;

import com.fast.gateway.entity.ApiQuotaLimitDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ApiQuotaLimitRepository {

    List<ApiQuotaLimitDO> listAllApiQuotaLimitConfig();

}
