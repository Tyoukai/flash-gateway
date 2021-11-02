package com.fast.gateway.others;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class MonoDemo {

    public static void main(String[] args) {
//        doOnErrorOrSuccess();
//        map();
//        then();
//        publishOn();
        doOnNext();
//        onErrorMap();
//        fromCallable();
//        defer();
//        timeout();
    }

    public static void fromCallable() {
        System.out.println("start:" + System.currentTimeMillis());
        Mono<String> s = Mono.fromCallable(() -> "a");

    }

    public static void timeout() {
        Mono.just("111").map(str -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return str;
        }).timeout(Duration.ofSeconds(1)).subscribe(System.out::println);
    }

    public static void defer() {
        Supplier<? extends Mono<? extends Integer>> supplier = new Supplier() {

            @Override
            public Mono<Integer> get() {
                return Mono.just(1111);
            }
        };
        Mono<Integer> integerMono = Mono.defer(supplier);
        integerMono.subscribe(System.out::println);

        Mono<Integer> integerMono1 = Mono.defer(() -> Mono.just(1111));
        integerMono1.subscribe(System.out::println);
    }

    public static void doOnErrorOrSuccess() {
        Mono.just(1.0)
                .map(n -> n / 10)
                .doOnError(e -> {
                    System.out.println("发生了错误");
                    e.printStackTrace();
                })
                .doOnSuccess(s -> {
                    System.out.println("执行成功:" + s);
                })
                .subscribe(System.out::println);
    }

    public static void doOnNext() {
        Map<String, String> map = new HashMap<>();
        Mono.just("sss")
                .doOnNext(s -> {
                    map.put(s, s);
                })
                .subscribe(s -> {
                    String t = s + "ttt";
                    map.put(t, t);
                });

//        Mono.just("sss")
//                .subscribe(new Observer<String>() {
//
//                })

        System.out.println(map);
    }

    public static void onErrorMap() {
        Mono.just(1)
                .map(s -> s / 1)
                .onErrorMap(e -> new Exception("error map"))
                .subscribe(System.out::println);
    }

    public static void publishOn() {
        Mono.just("sssss")
                .publishOn(Schedulers.newElastic("publishOn"))
                .filter(s -> {
                    System.out.println("current thread:" + Thread.currentThread().getName());
                    return s.startsWith("y");
                })
                .subscribe(System.out::println);
    }

    public static void map() {
        Mono a = Mono.just(1.0)
                .map(num -> num / 10)
                .map(num -> num / 10);
//                .subscribe(System.out::println);

        Mono b = Mono.just(1.0).flatMap(num -> Mono.just(num / 10));
    }

    public static void then() {
        Mono.just(1.0).then(Mono.just(2.0)).subscribe(System.out::println);
    }
}
