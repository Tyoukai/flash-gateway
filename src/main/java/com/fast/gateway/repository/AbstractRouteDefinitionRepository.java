package com.fast.gateway.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 动态路由更新抽象类
 * 路由介质由具体类实现，常用的有zk，redis，memcache等
 */
@Slf4j
public abstract class AbstractRouteDefinitionRepository implements RouteDefinitionRepository, ApplicationEventPublisherAware {

    private ApplicationEventPublisher applicationEventPublisher;


    @Override
    public Flux<RouteDefinition> getRouteDefinitions() {
        log.info("getRouteDefinitions start.time:{}", System.currentTimeMillis());
        return getRouteDefinitionsFromMedium();
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

    /**
     * 通知网关更新路由配置
     */
    public void refresh() {
        applicationEventPublisher.publishEvent(new RefreshRoutesEvent(this));
    }

    /**
     * 从不同存储介质中获取真正的路由配置信息
     *
     * @return
     */
    abstract Flux<RouteDefinition> getRouteDefinitionsFromMedium();
}
