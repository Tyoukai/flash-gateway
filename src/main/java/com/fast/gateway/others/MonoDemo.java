package com.fast.gateway.others;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class MonoDemo {

    public static void main(String[] args) {
//        doOnErrorOrSuccess();
//        map();
//        then();
//        publishOn();
//        doOnNext();
//        onErrorMap();
//        fromCallable();
//        defer();
//        timeout();
        diffWithDoOnSuccessAndDoOnNext();
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

    public static void diffWithDoOnSuccessAndDoOnNext() {
        Mono.just("sss")
                .doOnNext(System.out::println)
                .flatMap(i -> Mono.empty())
                .doOnNext(s -> {
                    System.out.println("is called");
                })
                .doOnSuccess(t -> {
                    System.out.println("doOnSuccess");
                })
                .block();
    }

    public static void doOnNext() {
        Map<String, String> map = new HashMap<>();
        List<String> list = new ArrayList<>();
        Mono.just("sss")
                .map(str -> {
                    str = str + "map";
                    list.add(str);
                    map.put(str, str);
                    return str;
                })
                .doOnNext(s -> {
                    map.put(s, s);
                    list.add(s);
                })
                .subscribe(s -> {
                    String t = s + "ttt";
                    map.put(t, t);
                    list.add(t);
                });

//        Mono.just("sss")
//                .subscribe(new Observer<String>() {
//
//                })

        System.out.println(map);
        System.out.println(list);
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
