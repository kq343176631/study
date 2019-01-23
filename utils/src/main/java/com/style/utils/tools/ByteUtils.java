package com.style.utils.tools;

/**
 * 字节转换工具
 */
public class ByteUtils {

    private static final int UNIT = 1024;

    /**
     * 计算字节大小 B,KB,MB,GB,TB,PB
     *
     * @param byteSize 字节
     * @return String
     */
    public static String formatByteSize(long byteSize) {

        if (byteSize <= -1) {
            return String.valueOf(byteSize);
        }

        double size = 1.0 * byteSize;

        String type;
        if ((int) Math.floor(size / UNIT) <= 0) { //不足1KB
            type = "B";
            return formatByteSize(size, type);
        }

        size = size / UNIT;
        if ((int) Math.floor(size / UNIT) <= 0) { //不足1MB
            type = "KB";
            return formatByteSize(size, type);
        }

        size = size / UNIT;
        if ((int) Math.floor(size / UNIT) <= 0) { //不足1GB
            type = "MB";
            return formatByteSize(size, type);
        }

        size = size / UNIT;
        if ((int) Math.floor(size / UNIT) <= 0) { //不足1TB
            type = "GB";
            return formatByteSize(size, type);
        }

        size = size / UNIT;
        if ((int) Math.floor(size / UNIT) <= 0) { //不足1PB
            type = "TB";
            return formatByteSize(size, type);
        }

        size = size / UNIT;
        if ((int) Math.floor(size / UNIT) <= 0) {
            type = "PB";
            return formatByteSize(size, type);
        }
        return ">PB";
    }

    /**
     * 将字节大小换算为指定单位大小
     *
     * @param byteSize byteSize
     * @param unit     unit
     * @return String
     */
    private static String formatByteSize(double byteSize, String unit) {

        int precision;

        if (byteSize * 1000 % 10 > 0) {
            precision = 3;
        } else if (byteSize * 100 % 10 > 0) {
            precision = 2;
        } else if (byteSize * 10 % 10 > 0) {
            precision = 1;
        } else {
            precision = 0;
        }

        String formatStr = "%." + precision + "f";

        if ("KB".equals(unit)) {
            return String.format(formatStr, (byteSize)) + "KB";
        } else if ("MB".equals(unit)) {
            return String.format(formatStr, (byteSize)) + "MB";
        } else if ("GB".equals(unit)) {
            return String.format(formatStr, (byteSize)) + "GB";
        } else if ("TB".equals(unit)) {
            return String.format(formatStr, (byteSize)) + "TB";
        } else if ("PB".equals(unit)) {
            return String.format(formatStr, (byteSize)) + "PB";
        }
        return String.format(formatStr, (byteSize)) + "B";
    }
}
