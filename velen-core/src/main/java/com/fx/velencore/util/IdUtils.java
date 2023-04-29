package com.fx.velencore.util;

import org.springframework.beans.factory.annotation.Value;

public final class IdUtils {

    public static void main(String[] args) {
        IdUtils idUtils = new IdUtils();
        for (int i = 0; i < 25; i++) {
            System.out.println(idUtils.getNextId());
        }
    }

    public static String getId() {
        return "1025-流月忆";
    }

    // 起始时间戳  1995-10-06 00:00:00
    private final static long START_TIME_STAMP = 812908800731L;

    // 每部分的位数
    private final static long SEQUENCE_BIT = 12; // 序列号占用位数
    private final static long MACHINE_BIT = 5; // 机器id占用位数
    private final static long DATACENTER_BIT = 5; // 机房id占用位数

    // 每部分最大值
    private final static long MAX_DATACENTER_NUM = ~(-1L << DATACENTER_BIT);
    private final static long MAX_MACHINE_NUM = ~(-1L << MACHINE_BIT);
    private final static long MAX_SEQUENCE = ~(-1L << SEQUENCE_BIT);

    // 每部分向左的位移
    private final static long MACHINE_LEFT = SEQUENCE_BIT;
    private final static long DATACENTER_LEFT = SEQUENCE_BIT + MACHINE_BIT;
    private final static long TIMESTAMP_LEFT = DATACENTER_LEFT + DATACENTER_BIT;

    @Value("${snow-flake.datacenter-id:0}")
    private long datacenterId; // 机房id
    @Value("${snow-flake.machine-id:0}")
    private long machineId; // 机器id
    private long sequence = 0L; // 序列号
    private long lastTimeStamp = -1L; // 上次的时间戳

    public IdUtils() {
        if (datacenterId > MAX_DATACENTER_NUM || datacenterId < 0) {
            throw new IllegalArgumentException("datacenterId can't be greater than MAX_DATACENTER_NUM or less than 0");
        }
        if (machineId > MAX_MACHINE_NUM || machineId < 0) {
            throw new IllegalArgumentException("machineId can't be greater than MAX_MACHINE_NUM or less than 0");
        }
    }

    // 产生下一个ID
    public synchronized long getNextId() {
        long currentTimeStamp = System.currentTimeMillis();
        if (currentTimeStamp < lastTimeStamp) {
            throw new RuntimeException("Clock moved backwards.Refusing to generate id");
        }
        if (currentTimeStamp == lastTimeStamp) {
            // 若在相同毫秒内 序列号自增
            sequence = (sequence + 1) & MAX_SEQUENCE;
            // 同一毫秒的序列数已达到最大
            if (sequence == 0L) {
                currentTimeStamp = getNextMill();
            }
        } else {
            // 若在不同毫秒内 则序列号置为0
            sequence = 0L;
        }
        lastTimeStamp = currentTimeStamp;

        return (currentTimeStamp - START_TIME_STAMP) << TIMESTAMP_LEFT // 时间戳部分
                | datacenterId << DATACENTER_LEFT // 机房id部分
                | machineId << MACHINE_LEFT // 机器id部分
                | sequence; // 序列号部分
    }

    // 获取新的毫秒数
    private long getNextMill() {
        long currentTimeStamp = System.currentTimeMillis();
        while (currentTimeStamp <= lastTimeStamp) {
            currentTimeStamp = System.currentTimeMillis();
        }
        return currentTimeStamp;
    }

}

