package com.fast.gateway.service;

import com.fast.gateway.common.DataConvert;
import com.fast.gateway.entity.ApiRouteConfigDO;
import com.fast.gateway.entity.ApiRouteConfigDTO;
import com.fast.gateway.repository.ApiRouteRepository;
import com.fast.gateway.utils.ObjectMapperUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.fast.gateway.utils.Constants.SPILT_SLASH;

/**
 *  时间相关的所有服务统一到该service进行处理
 *  1、同步动态路由
 *  2、限额配置同步
 *  3、限流配置同步
 */
@Service
@Slf4j
public class TimeService {

    @Autowired
    private ApiRouteRepository apiRouteRepository;

    private static ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

    private static String PATH = "/gateway";
    private CuratorFramework curatorClient;
    private static String ZOOKEEPER_ADDRESS = "42.192.49.234:2181";

    @PostConstruct
    public void init() {
        executorService.scheduleAtFixedRate(this::syncRouteToZk, 0, 10, TimeUnit.SECONDS);

        curatorClient = CuratorFrameworkFactory.builder()
                .connectString(ZOOKEEPER_ADDRESS)
                .connectionTimeoutMs(2000)
                .sessionTimeoutMs(10000)
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .build();

        curatorClient.start();
    }

    private void syncRouteToZk() {
        List<ApiRouteConfigDO> apiRouteConfigDOS = apiRouteRepository.listApiRouteConfig();
        List<ApiRouteConfigDTO> apiRouteConfigDTOS = DataConvert.toDto(apiRouteConfigDOS);
        for (ApiRouteConfigDTO apiRouteConfigDTO : apiRouteConfigDTOS) {
            String routeStrInDb = ObjectMapperUtils.toJson(apiRouteConfigDTO);
            String routeStrInZk = "";

            String path = PATH + SPILT_SLASH + apiRouteConfigDTO.getId();
            try {
                routeStrInZk = new String(curatorClient.getData().forPath(path));
            } catch (Exception e) {
                log.error("get data from zk error. ", e);
            }

            if (!StringUtils.equals(routeStrInDb, routeStrInZk)) {
                try {
                    if (curatorClient.checkExists().forPath(path) == null) {
                        curatorClient.create().forPath(path, routeStrInDb.getBytes());
                    } else {
                        curatorClient.setData().forPath(path, routeStrInDb.getBytes());
                    }
                } catch (Exception e) {
                    log.error("check data from zk error.", e);
                }
            }
        }
    }
}
