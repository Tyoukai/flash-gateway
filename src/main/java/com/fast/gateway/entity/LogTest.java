package com.fast.gateway.entity;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class LogTest {
    private int a;
    private String b;

    public static void main(String[] args) {
        LogTest logTest = new LogTest();
        logTest.setA(1);
        logTest.setB("b");
        System.out.println(logTest);
        log.info("logTest:{}", logTest);
    }
}
