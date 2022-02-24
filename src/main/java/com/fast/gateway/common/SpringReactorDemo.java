package com.fast.gateway.common;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class SpringReactorDemo {

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
//        diffWithDoOnSuccessAndDoOnNext();
        fromCallable();
//        subscribeTheory();
//        threadTest();
//        threadTest1();
    }

    public static void threadTest() {
        long s = System.currentTimeMillis();

        System.out.println("当前线程：" + Thread.currentThread().getName());

        for(int i = 0; i < 10; i++) {
            Flux.just("Tom")
                    .map(s1 -> {
                        System.out.println("map 当前线程：" + Thread.currentThread().getName());
                        return s1 + "111";
                    })
                    .publishOn(Schedulers.newElastic("publishOn-"))
                    .map(str -> {
                        System.out.println("map1当前线程：" + Thread.currentThread().getName());
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        return str.concat("@qq.com");
                    })
                    .subscribe(con -> {
                        System.out.println("所花费时间：" + (System.currentTimeMillis() - s) + ",最终运行结果：" + con + "，当前所在线程：" + Thread.currentThread().getName());
                    });
        }

        System.out.println("花费时间：" + (System.currentTimeMillis() - s) + ", 当前所在线程:" + Thread.currentThread().getName());
    }

    public static void threadTest1() {
        long s = System.currentTimeMillis();

        System.out.println("当前线程：" + Thread.currentThread().getName());

        for(int i = 0; i < 10; i++) {
            Flux.just("Tom")
                    .map(s1 -> {
                        System.out.println("map 当前线程：" + Thread.currentThread().getName());
                        return s1 + "111";
                    })
                    .map(str -> {
                        System.out.println("map1当前线程：" + Thread.currentThread().getName());
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        return str.concat("@qq.com");
                    })
                    .subscribe(con -> {
                        System.out.println("所花费时间：" + (System.currentTimeMillis() - s) + ",最终运行结果：" + con + "，当前所在线程：" + Thread.currentThread().getName());
                    });
        }

        System.out.println("花费时间：" + (System.currentTimeMillis() - s) + ",当前所在线程：" + Thread.currentThread().getName());
    }

    public static void subscribeTheory() {
        Flux.just("tom", "jack", "allen")
                .map(s -> s.concat("@qq.com"))
                .publishOn(Schedulers.elastic())
                .filter(s -> s.length() > 3)
                .subscribe(System.out::println);
    }

    public static void subscribeMono() {
        Mono.just("tom")
                .map(s -> s.concat("@qq.com"))
                .filter(s -> s.length() > 3)
                .subscribe(System.out::println);
    }

    public static void fromCallable() {
        long start = System.currentTimeMillis();
        Mono.fromCallable(() -> {
            Thread.sleep(1000);
            System.out.println("当前线程:" + Thread.currentThread().getName() + ", 耗时：" + (System.currentTimeMillis() - start));
            return "a";
        }).subscribe(System.out::println);

        System.out.println("当前线程:：" + Thread.currentThread().getName() + ", 耗时：" + (System.currentTimeMillis() - start));
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
        Supplier<? extends Mono<? extends Integer>> supplier = () -> Mono.just(1111);
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

    /**
     * doOnSuccess:在序列发布成功后触发，序列结果可能为T或null
     * doOnNext:在序列发布后触发，序列的结果必须为T才能触发
     */
    public static void diffWithDoOnSuccessAndDoOnNext() {
        Mono.just("sss")
                .doOnNext(System.out::println)
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
