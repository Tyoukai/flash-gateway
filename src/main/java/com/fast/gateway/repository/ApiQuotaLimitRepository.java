package com.fast.gateway.repository;

import com.fast.gateway.entity.ApiQuotaLimit;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ApiQuotaLimitRepository {

    List<ApiQuotaLimit> getAllApiQuotaLimitConfig();

}
