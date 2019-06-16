package com.style.study.socket.utils;

import com.style.study.socket.msg.CommonMessage;
import com.style.study.socket.msg.ResponseMessage;
import com.style.utils.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ConvertUtils {

    private static final String DEFAULT_CHART_SET = "UTF-8";

    private static final int BYTE_JAVA_BYTES = 1;

    private static final int SHORT_JAVA_BYTES = 2;

    private static final int INT_JAVA_BYTES = 4;

    private static final int FLOAT_JAVA_BYTES = 4;

    private static final int DOUBLE_JAVA_BYTES = 8;

    private static final int LONG_JAVA_BYTES = 8;

    private static final int ONE_BYTES_BITS = 8;

    private static final int TWO_BYTES_BITS = ONE_BYTES_BITS * 2;

    private static final int THREE_BYTES_BITS = ONE_BYTES_BITS * 3;

    private static final int FOUR_BYTES_BITS = ONE_BYTES_BITS * 4;

    private static final int FIVE_BYTES_BITS = ONE_BYTES_BITS * 5;

    private static final int SIX_BYTES_BITS = ONE_BYTES_BITS * 6;

    private static final int SEVEN_BYTES_BITS = ONE_BYTES_BITS * 7;

    private static final int BYTE_MAX_VALUE = -1;

    private static final int IDX_MAX_POS = 3;


    private static Logger logger = LoggerFactory.getLogger(ConvertUtils.class);

    public static ByteBuffer responseToByteBuffer(ResponseMessage response, ByteOrder order) {

        return null;
    }

    public static CommonMessage byteBufferToRequest(ByteBuffer byteBuffer, ByteOrder littleEndian) {

        return null;
    }

    /**
     * 基本数据类型转换为字节数组
     * <p>int类型</p>
     *
     * @param value 待转换的值
     * @return 字节数组
     */
    public static byte[] byteToByteArray(byte value) {
        if (value == '\0') {
            logger.error("Value is null....");
            return new byte[0];
        }
        byte[] array = new byte[BYTE_JAVA_BYTES];
        array[BYTE_JAVA_BYTES - 1] = value;
        return array;
    }

    /**
     * 字节数组转换为基本数据类型
     * <p>byte类型</p>
     *
     * @param value 待转换的值
     * @return 字节数组
     */
    public static byte byteArrayToByte(byte[] value) {
        if (value == null) {
            logger.error("Value order is null....");
            return '\0';
        }
        if (value.length != BYTE_JAVA_BYTES) {
            logger.error("Value order is null....");
            return '\0';
        }
        return value[BYTE_JAVA_BYTES - 1];
    }

    /**
     * 基本数据类型转换为字节数组
     * <p>short类型</p>
     *
     * @param value     待转换的值
     * @param byteOrder 字节序
     * @return 字节数组
     */
    public static byte[] shortToByteArray(short value, ByteOrder byteOrder) {
        if (byteOrder == null) {
            logger.error("Byte order is null....");
            return new byte[0];
        }
        byte[] array = new byte[SHORT_JAVA_BYTES];
        if (byteOrder == ByteOrder.BIG_ENDIAN) {
            byte pos = 0;
            array[pos++] = (byte) (value >> ONE_BYTES_BITS);
            array[pos] = (byte) value;
        } else if (byteOrder == ByteOrder.LITTLE_ENDIAN) {
            byte pos = 0;
            array[pos++] = (byte) value;
            array[pos] = (byte) (value >> ONE_BYTES_BITS);
        } else {
            logger.error("Byte order is invalid...");
        }
        return array;
    }

    /**
     * 字节数组转换为基本数据类型
     * <p>short类型</p>
     *
     * @param value 待转换的值
     * @return 字节数组
     */
    public static short byteArrayToShort(byte[] value, ByteOrder byteOrder) {
        if (value == null) {
            logger.error("Value order is null....");
            return '\0';
        }
        if (byteOrder == null) {
            logger.error("Byte order order is null....");
            return '\0';
        }
        if (value.length != SHORT_JAVA_BYTES) {
            logger.error("Value order is null....");
            return '\0';
        }
        byte pos = 0;
        short sv = 0;
        short sBase;
        if (byteOrder == ByteOrder.BIG_ENDIAN) {
            for (int i = SHORT_JAVA_BYTES - 1; i >= 0; i--) {
                sBase = unsignedByteToShort(value[pos++]);
                sv |= sBase << (i * ONE_BYTES_BITS);
            }
        } else if (byteOrder == ByteOrder.LITTLE_ENDIAN) {
            for (int i = 0; i < SHORT_JAVA_BYTES; i++) {
                sBase = unsignedByteToShort(value[pos++]);
                sv |= sBase << (i * ONE_BYTES_BITS);
            }
        } else {
            logger.error("Byte order is invalid...");
        }

        return sv;
    }

    public static short unsignedByteToShort(byte value) {
        if (value < 0) {
            return (short) ((1 << ONE_BYTES_BITS) + value);
        }
        return value;
    }

    public static int unsignedByteToInt(byte value) {
        if (value < 0) {
            return (1 << ONE_BYTES_BITS) + value;
        }
        return value;
    }

    public static int unsignedShortToInt(short value) {
        if (value < 0) {
            return (1 << TWO_BYTES_BITS) + value;
        }
        return value;
    }

    public static long unsignedIntToLong(int value) {
        long IBase = 1;
        if (value < 0) {
            return (IBase << FOUR_BYTES_BITS) + value;
        }
        return value;
    }

    /**
     * 基本数据类型转换为字节数组
     * <p>int类型</p>
     *
     * @param value     待转换的值
     * @param byteOrder 字节序
     * @return 字节数组
     */
    public static byte[] intToByteArray(int value, ByteOrder byteOrder) {
        if (byteOrder == null) {
            logger.error("Byte order is null....");
            return new byte[0];
        }
        byte[] array = new byte[INT_JAVA_BYTES];
        if (byteOrder == ByteOrder.BIG_ENDIAN) {
            byte pos = 0;
            array[pos++] = (byte) (value >> THREE_BYTES_BITS);
            array[pos++] = (byte) (value >> TWO_BYTES_BITS);
            array[pos++] = (byte) (value >> ONE_BYTES_BITS);
            array[pos] = (byte) value;
        } else if (byteOrder == ByteOrder.LITTLE_ENDIAN) {
            for (int idx = 0; idx < INT_JAVA_BYTES; idx++) {
                array[idx] = (byte) (value >> idx * ONE_BYTES_BITS);
            }
        } else {
            logger.error("Byte order is invalid...");
        }
        return array;
    }

    /**
     * 字节数组转换为基本数据类型
     * <p>byte类型</p>
     *
     * @param value 待转换的值
     * @return 字节数组
     */
    @SuppressWarnings("all")
    public static int byteArrayToInt(byte[] value, ByteOrder byteOrder) {
        if (value == null) {
            logger.error("Value order is null....");
            return '\0';
        }
        if (byteOrder == null) {
            logger.error("Byte order order is null....");
            return '\0';
        }
        if (value.length != INT_JAVA_BYTES) {
            logger.error("Value order is null....");
            return '\0';
        }
        int pos = 0;
        int iv = 0;
        int iBase = 0;
        if (byteOrder == ByteOrder.BIG_ENDIAN) {
            for (int i = INT_JAVA_BYTES - 1; i >= 0; i--) {
                iBase = unsignedByteToShort(value[pos++]);
                iv |= iBase << (i * ONE_BYTES_BITS);

            }
        } else if (byteOrder == ByteOrder.LITTLE_ENDIAN) {
            for (int i = 0; i < INT_JAVA_BYTES; i++) {
                iBase = unsignedByteToShort(value[pos++]);
                iv |= iBase << (i * ONE_BYTES_BITS);
            }

        } else {
            logger.error("Byte order is invalid...");
            return '\0';
        }
        return iv;
    }

    /**
     * 基本数据类型转换为字节数组
     * <p>float类型</p>
     *
     * @param value     待转换的值
     * @param byteOrder 字节序
     * @return 字节数组
     */
    public static byte[] floatToByteArray(float value, ByteOrder byteOrder) {
        if (byteOrder == null) {
            logger.error("Byte order is null....");
            return new byte[0];
        }
        return intToByteArray(Float.floatToRawIntBits(value), byteOrder);
    }

    /**
     * 字节数组转换为基本数据类型
     * <p>byte类型</p>
     *
     * @param value 待转换的值
     * @return 字节数组
     */
    public static float byteArrayToFloat(byte[] value, ByteOrder byteOrder) {
        if (value == null) {
            logger.error("Value order is null....");
            return '\0';
        }
        if (byteOrder == null) {
            logger.error("Byte order order is null....");
            return '\0';
        }
        if (value.length != FLOAT_JAVA_BYTES) {
            logger.error("Value order is null....");
            return '\0';
        }
        return Float.intBitsToFloat(byteArrayToInt(value, byteOrder));
    }

    /**
     * 基本数据类型转换为字节数组
     * <p>long类型</p>
     *
     * @param value 待转换的值
     * @return 字节数组
     */
    public static byte[] longToByteArray(long value, ByteOrder byteOrder) {
        if (byteOrder == null) {
            logger.error("Byte order is null....");
            return new byte[0];
        }
        byte[] array = new byte[LONG_JAVA_BYTES];
        if (byteOrder == ByteOrder.BIG_ENDIAN) {
            byte pos = 0;
            array[pos++] = (byte) ((value >> SEVEN_BYTES_BITS) & BYTE_MAX_VALUE);
            array[pos++] = (byte) ((value >> SIX_BYTES_BITS) & BYTE_MAX_VALUE);
            array[pos++] = (byte) ((value >> FIVE_BYTES_BITS) & BYTE_MAX_VALUE);
            array[pos++] = (byte) ((value >> FOUR_BYTES_BITS) & BYTE_MAX_VALUE);
            array[pos++] = (byte) ((value >> THREE_BYTES_BITS) & BYTE_MAX_VALUE);
            array[pos++] = (byte) ((value >> TWO_BYTES_BITS) & BYTE_MAX_VALUE);
            array[pos++] = (byte) ((value >> ONE_BYTES_BITS) & BYTE_MAX_VALUE);
            array[pos] = (byte) (value & BYTE_MAX_VALUE);
        } else if (byteOrder == ByteOrder.LITTLE_ENDIAN) {
            for (int idx = 0; idx < LONG_JAVA_BYTES; idx++) {

                array[idx] = (byte) ((value >> idx * ONE_BYTES_BITS) & BYTE_MAX_VALUE);
            }
        } else {
            logger.error("Byte order is invalid...");
        }
        return array;
    }

    /**
     * 字节数组转换为基本数据类型
     * <p>byte类型</p>
     *
     * @param value 待转换的值
     * @return 字节数组
     */
    @SuppressWarnings("all")
    public static long byteArrayToLong(byte[] value, ByteOrder byteOrder) {
        if (value == null) {
            logger.error("Value order is null....");
            return '\0';
        }
        if (byteOrder == null) {
            logger.error("Byte order order is null....");
            return '\0';
        }
        if (value.length != LONG_JAVA_BYTES) {
            logger.error("Value order is null....");
            return '\0';
        }
        int pos = 0;
        long lv = 0;
        long lBase = 0;
        if (byteOrder == ByteOrder.BIG_ENDIAN) {
            for (int i = LONG_JAVA_BYTES - 1; i >= 0; i--) {
                lBase = unsignedByteToShort(value[pos++]);
                lv |= lBase << (i * ONE_BYTES_BITS);

            }
        } else if (byteOrder == ByteOrder.LITTLE_ENDIAN) {
            for (int i = 0; i < LONG_JAVA_BYTES; i++) {
                lBase = unsignedByteToShort(value[pos++]);
                lv |= lBase << (i * ONE_BYTES_BITS);
            }

        } else {
            logger.error("Byte order is invalid...");
            return '\0';
        }
        return lv;
    }

    /**
     * 基本数据类型转换为字节数组
     * <p>double类型</p>
     *
     * @param value 待转换的值
     * @return 字节数组
     */
    public static byte[] doubleToByteArray(double value, ByteOrder byteOrder) {
        if (byteOrder == null) {
            logger.error("Byte order is null....");
            return new byte[0];
        }
        return longToByteArray(Double.doubleToRawLongBits(value), byteOrder);
    }

    /**
     * 字节数组转换为基本数据类型
     * <p>byte类型</p>
     *
     * @param value 待转换的值
     * @return 字节数组
     */
    public static double byteArrayToDouble(byte[] value, ByteOrder byteOrder) {
        if (value == null) {
            logger.error("Value order is null....");
            return '\0';
        }
        if (byteOrder == null) {
            logger.error("Byte order order is null....");
            return '\0';
        }
        if (value.length != DOUBLE_JAVA_BYTES) {
            logger.error("Value order is null....");
            return '\0';
        }
        return Double.longBitsToDouble(byteArrayToLong(value, byteOrder));
    }

    /**
     * 基本数据类型转换为字节数组
     * <p>string类型</p>
     *
     * @param value 待转换的值
     * @return 字节数组
     */
    public static byte[] stringToByteArray(String value) {
        if (StringUtils.isBlank(value)) {
            logger.error("Value is null....");
            return new byte[0];
        }
        try {
            return value.getBytes(DEFAULT_CHART_SET);
        } catch (UnsupportedEncodingException e) {
            logger.error("Convert String to byte array failed...", e);
            return new byte[0];
        }
    }

    /**
     * 字节数组转换为基本数据类型
     * <p>byte类型</p>
     *
     * @param value 待转换的值
     * @return 字节数组
     */
    public static String byteArrayToString(byte[] value) {
        if (value == null) {
            logger.error("Value order is null....");
            return null;
        }
        if (value.length <= 0) {
            logger.error("Value is null....");
            return null;
        }
        try {
            return new String(value, DEFAULT_CHART_SET).trim();
        } catch (UnsupportedEncodingException e) {
            logger.error("Failed to convert string...");
        }
        return null;
    }

    /**
     * 基本数据类型转换为字节数组
     * <p>char[]类型</p>
     *
     * @param value 待转换的值
     * @return 字节数组
     */
    public static byte[] charArrayToByteArray(char[] value) {
        if (value == null) {
            logger.error("Value is null....");
            return new byte[0];
        }
        try {
            return new String(value).getBytes(DEFAULT_CHART_SET);
        } catch (UnsupportedEncodingException e) {
            logger.error("Convert String to byte array failed...", e);
            return new byte[0];
        }
    }

    /**
     * 字节数组转换为基本数据类型
     * <p>byte类型</p>
     *
     * @param value 待转换的值
     * @return 字节数组
     */
    public static char[] byteArrayToCharArray(byte[] value) {
        if (value == null) {
            logger.error("Value order is null....");
            return new char[0];
        }
        if (value.length <= 0) {
            logger.error("Value is null....");
            return new char[0];
        }
        try {
            return new String(value, DEFAULT_CHART_SET).trim().toCharArray();
        } catch (UnsupportedEncodingException e) {
            logger.error("Failed to convert char array...");
        }
        return null;
    }

}
