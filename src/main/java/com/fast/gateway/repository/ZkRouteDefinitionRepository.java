package com.fast.gateway.repository;

import com.fast.gateway.utils.ObjectMapperUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

import static com.fast.gateway.utils.Constants.SPILT_SLASH;


/**
 * 动态路由配置，存储介质zookeeper
 */
@Component
public class ZkRouteDefinitionRepository implements RouteDefinitionRepository, ApplicationEventPublisherAware {

    private ApplicationEventPublisher applicationEventPublisher;
    private String path = "/gateway";
    private CuratorFramework curatorClient;
    private String zookeeperAddress = "42.192.49.234:2181";

    @PostConstruct
    public void init() throws Exception {
        curatorClient = CuratorFrameworkFactory.builder()
                .connectString(zookeeperAddress)
                .connectionTimeoutMs(2000)
                .sessionTimeoutMs(10000)
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .build();

        curatorClient.start();

        PathChildrenCache cache = new PathChildrenCache(curatorClient, path, true);
        cache.getListenable().addListener((curatorFramework, event) -> {
            ChildData data = event.getData();
            if (data.getData() == null) {
                return;
            }
            String routeStr = new String(data.getData());
            switch (event.getType()) {
                case CHILD_ADDED:
                    System.out.println("新路由创建," + routeStr);
                    break;
                case CHILD_UPDATED:
                    System.out.println("路由被修改, " + routeStr);
                    break;
                case CHILD_REMOVED:
                    System.out.println("路由被删除," + routeStr);
            }
            applicationEventPublisher.publishEvent(new RefreshRoutesEvent(this));
        });

        cache.start(PathChildrenCache.StartMode.BUILD_INITIAL_CACHE);

    }

    @Override
    public Flux<RouteDefinition> getRouteDefinitions() {
        List<RouteDefinition> routeDefinitions = new ArrayList<>();
        try {
            List<String> childIds = curatorClient.getChildren().forPath(path);
            childIds.forEach(childId -> {
                String routeStr;
                try {
                    routeStr = new String(curatorClient.getData().forPath(path + SPILT_SLASH + childId));
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

    @Override
    public Mono<Void> save(Mono<RouteDefinition> route) {
        return route.flatMap(routeDefinition -> Mono.empty());
    }

    @Override
    public Mono<Void> delete(Mono<String> routeId) {
        return Mono.empty();
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }
}
