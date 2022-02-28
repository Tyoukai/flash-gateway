package com.fast.gateway.repository;

import com.fast.gateway.utils.ObjectMapperUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

import static com.fast.gateway.utils.Constants.SPILT_SLASH;

/**
 * 根据存储介质的不同实现不同的AbstractRouteDefinitionRepository，默认通过zookeeper存储
 */
@Component
@Slf4j
public class ZkRouteDefinitionRepository extends AbstractRouteDefinitionRepository {

    private static String PATH = "/gateway";
    private CuratorFramework curatorClient;
    private static String ZOOKEEPER_ADDRESS = "42.192.49.234:2181";

    @PostConstruct
    public void init() throws Exception {
        curatorClient = CuratorFrameworkFactory.builder()
                .connectString(ZOOKEEPER_ADDRESS)
                .connectionTimeoutMs(2000)
                .sessionTimeoutMs(10000)
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .build();

        curatorClient.start();

        PathChildrenCache cache = new PathChildrenCache(curatorClient, PATH, true);
        cache.getListenable().addListener((curatorFramework, event) -> {
            ChildData data = event.getData();
            if (data.getData() == null) {
                return;
            }
            String routeStr = new String(data.getData());
            switch (event.getType()) {
                case CHILD_ADDED:
                    log.info("新路由创建:{}" + routeStr);
                    break;
                case CHILD_UPDATED:
                    log.info("路由被修改:{}" + routeStr);
                    break;
                case CHILD_REMOVED:
                    log.info("路由被删除:{}" + routeStr);
            }
            refresh();
        });

        cache.start(PathChildrenCache.StartMode.BUILD_INITIAL_CACHE);
    }

    @Override
    Flux<RouteDefinition> getRouteDefinitionsFromMedium() {
        List<RouteDefinition> routeDefinitions = new ArrayList<>();
        try {
            List<String> childIds = curatorClient.getChildren().forPath(PATH);
            childIds.forEach(childId -> {
                String routeStr;
                try {
                    routeStr = new String(curatorClient.getData().forPath(PATH + SPILT_SLASH + childId));
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
                RouteDefinition routeDefinition = ObjectMapperUtils.fromJson(routeStr, RouteDefinition.class);
                routeDefinitions.add(routeDefinition);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Flux.fromIterable(routeDefinitions);
    }
}
