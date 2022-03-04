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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.fast.gateway.utils.Constants.SPILT_SLASH;

/**
 *  数据同步配置
 *  1、同步动态路由
 *  原因：路由的原始信息存储在db中，路由相关的参数配置有版本的概念，为了解耦
 *  数据同步等相关操作，ZkRouteDefinitionRepository拉取的仅仅只是zk或缓存中的
 *  数据，相关配置及版本信息交由该服务配置。后续随着规模的扩大，该模块可以与
 *  运行转发模块独立开来。
 *  目前通过轮询对比的方式来更新路由，后续可以通过监听数据库配置的变化来完成
 *  2、同步参数映射
 *  原因同上
 */
@Service
@Slf4j
public class DataSyncService {

    @Autowired
    private ApiRouteRepository apiRouteRepository;

    private static ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

    @Value("${flashGatewayDynamicRoutePath}")
    private String dynamicRoutePath;

    private CuratorFramework curatorClient;
    @Value("${spring.cloud.zookeeper.connect-string}")
    private String zookeeperAddress;

    @PostConstruct
    public void init() {
        executorService.scheduleAtFixedRate(this::syncRouteToZk, 0, 10, TimeUnit.SECONDS);

        curatorClient = CuratorFrameworkFactory.builder()
                .connectString(zookeeperAddress)
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

            String path = dynamicRoutePath + SPILT_SLASH + apiRouteConfigDTO.getId();
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
