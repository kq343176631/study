package com.style.common.id;

import com.style.common.SystemUtils;

import java.util.Random;

/**
 * 来自于twitter项目snowflake的id产生方案，全局唯一，时间有序。
 * 64位ID (42(毫秒)+5(机器ID)+5(业务编码)+12(重复累加))
 *
 * @version 2018-11-11
 */
public class IdWorker {

    private final static long twEpoch = 1288834974657L;
    // 机器标识位数
    private final static long workerIdBits = 5L;
    // 数据中心标识位数
    private final static long dataCenterIdBits = 5L;
    // 机器ID最大值
    private final static long maxWorkerId = -1L ^ (-1L << workerIdBits);
    // 数据中心ID最大值
    private final static long maxDataCenterId = -1L ^ (-1L << dataCenterIdBits);
    // 毫秒内自增位
    private final static long sequenceBits = 12L;
    // 机器ID偏左移12位
    private final static long workerIdShift = sequenceBits;
    // 数据中心ID左移17位
    private final static long dataCenterIdShift = sequenceBits + workerIdBits;
    // 时间毫秒左移22位
    private final static long timestampLeftShift = sequenceBits + workerIdBits + dataCenterIdBits;

    private final static long sequenceMask = -1L ^ (-1L << sequenceBits);

    private static long lastTimestamp = -1L;

    private long sequence = 0L;

    private final long workerId;

    private final long dataCenterId;

    public IdWorker(long workerId, long dataCenterId) {
        if (workerId > maxWorkerId || workerId < 0) {
            if (workerId == -1) {
                this.workerId = new Random().nextInt((int) maxWorkerId);
            } else {
                throw new IllegalArgumentException(
                        "worker Id can't be greater than %d or less than 0");
            }
        } else {
            this.workerId = workerId;
        }
        if (dataCenterId > maxDataCenterId || dataCenterId < 0) {
            if (dataCenterId == -1) {
                this.dataCenterId = new Random().nextInt((int) maxDataCenterId);
            } else {
                throw new IllegalArgumentException(
                        "dataCenterId Id can't be greater than %d or less than 0");
            }
        } else {
            this.dataCenterId = dataCenterId;
        }
    }

    public synchronized long nextId() {
        long timestamp = timeGen();
        if (timestamp < lastTimestamp) {
            try {
                throw new Exception(
                        "Clock moved backwards.  Refusing to generate id for "
                                + (lastTimestamp - timestamp) + " milliseconds");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (lastTimestamp == timestamp) {
            // 当前毫秒内，则+1
            sequence = (sequence + 1) & sequenceMask;
            if (sequence == 0) {
                // 当前毫秒内计数满了，则等待下一秒
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0;
        }

        lastTimestamp = timestamp;
        // ID偏移组合生成最终的ID，并返回ID
        return ((timestamp - twEpoch) << timestampLeftShift)
                | (dataCenterId << dataCenterIdShift)
                | (workerId << workerIdShift) | sequence;
    }

    private long tilNextMillis(final long lastTimestamp) {
        long timestamp = this.timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = this.timeGen();
        }
        return timestamp;
    }

    private long timeGen() {
        return SystemUtils.now();
    }
}