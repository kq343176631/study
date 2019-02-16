package com.style.common.network;

import com.style.common.filter.FilterUtils;
import com.style.common.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * IpUtils
 */
public class NetUtils {

    /**
     * 获取客户端IP地址
     */
    public static String getRemoteAddress(HttpServletRequest request) {
        if (request == null) {
            return "unknown";
        }
        String ip = null;
        //String xffName = PropertyUtils.getInstance().getProperty("shiro.remoteAddrHeaderName");
        String xffName ="";
        if (StringUtils.isNotBlank(xffName)) {
            ip = request.getHeader(xffName);
        }
        if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (StringUtils.isNotBlank(ip)) {
            ip = FilterUtils.doXssFilter(ip);
            ip = StringUtils.split(ip, ",")[0];
        }
        if (StringUtils.isBlank(ip)) {
            ip = "unknown";
        }
        return ip;
    }

    public static byte[] textToNumericFormatV4(String paramString) {
        if (paramString.length() == 0) {
            return null;
        }
        byte[] arrayOfByte = new byte[4];
        String[] arrayOfString = paramString.split("\\.", -1);
        try {
            long l;
            int i;
            switch (arrayOfString.length) {
                case 1:
                    l = Long.parseLong(arrayOfString[0]);
                    if ((l < 0L) || (l > 4294967295L)) {
                        return null;
                    }
                    arrayOfByte[0] = ((byte) (int) (l >> 24 & 0xFF));
                    arrayOfByte[1] = ((byte) (int) ((l & 0xFFFFFF) >> 16 & 0xFF));
                    arrayOfByte[2] = ((byte) (int) ((l & 0xFFFF) >> 8 & 0xFF));
                    arrayOfByte[3] = ((byte) (int) (l & 0xFF));
                    break;
                case 2:
                    l = Integer.parseInt(arrayOfString[0]);
                    if ((l < 0L) || (l > 255L)) {
                        return null;
                    }
                    arrayOfByte[0] = ((byte) (int) (l & 0xFF));
                    l = Integer.parseInt(arrayOfString[1]);
                    if ((l < 0L) || (l > 16777215L)) {
                        return null;
                    }
                    arrayOfByte[1] = ((byte) (int) (l >> 16 & 0xFF));
                    arrayOfByte[2] = ((byte) (int) ((l & 0xFFFF) >> 8 & 0xFF));
                    arrayOfByte[3] = ((byte) (int) (l & 0xFF));
                    break;
                case 3:
                    for (i = 0; i < 2; i++) {
                        l = Integer.parseInt(arrayOfString[i]);
                        if ((l < 0L) || (l > 255L)) {
                            return null;
                        }
                        arrayOfByte[i] = ((byte) (int) (l & 0xFF));
                    }
                    l = Integer.parseInt(arrayOfString[2]);
                    if ((l < 0L) || (l > 65535L)) {
                        return null;
                    }
                    arrayOfByte[2] = ((byte) (int) (l >> 8 & 0xFF));
                    arrayOfByte[3] = ((byte) (int) (l & 0xFF));
                    break;
                case 4:
                    for (i = 0; i < 4; i++) {
                        l = Integer.parseInt(arrayOfString[i]);
                        if ((l < 0L) || (l > 255L)) {
                            return null;
                        }
                        arrayOfByte[i] = ((byte) (int) (l & 0xFF));
                    }
                    break;
                default:
                    return null;
            }
        } catch (NumberFormatException localNumberFormatException) {
            return null;
        }
        return arrayOfByte;
    }

    public static byte[] textToNumericFormatV6(String paramString) {
        if (paramString.length() < 2) {
            return null;
        }
        char[] arrayOfChar = paramString.toCharArray();
        byte[] arrayOfByte1 = new byte[16];

        int m = arrayOfChar.length;
        int n = paramString.indexOf("%");
        if (n == m - 1) {
            return null;
        }
        if (n != -1) {
            m = n;
        }
        int i = -1;
        int i1 = 0;
        int i2 = 0;
        if ((arrayOfChar[i1] == ':') && (arrayOfChar[(++i1)] != ':')) {
            return null;
        }
        int i3 = i1;
        int j = 0;
        int k = 0;
        int i4;
        while (i1 < m) {
            char c = arrayOfChar[(i1++)];
            i4 = Character.digit(c, 16);
            if (i4 != -1) {
                k <<= 4;
                k |= i4;
                if (k > 65535) {
                    return null;
                }
                j = 1;
            } else if (c == ':') {
                i3 = i1;
                if (j == 0) {
                    if (i != -1) {
                        return null;
                    }
                    i = i2;
                } else {
                    if (i1 == m) {
                        return null;
                    }
                    if (i2 + 2 > 16) {
                        return null;
                    }
                    arrayOfByte1[(i2++)] = ((byte) (k >> 8 & 0xFF));
                    arrayOfByte1[(i2++)] = ((byte) (k & 0xFF));
                    j = 0;
                    k = 0;
                }
            } else if ((c == '.') && (i2 + 4 <= 16)) {
                String str = paramString.substring(i3, m);

                int i5 = 0;
                int i6 = 0;
                while ((i6 = str.indexOf('.', i6)) != -1) {
                    i5++;
                    i6++;
                }
                if (i5 != 3) {
                    return null;
                }
                byte[] arrayOfByte3 = textToNumericFormatV4(str);
                if (arrayOfByte3 == null) {
                    return null;
                }
                for (int i7 = 0; i7 < 4; i7++) {
                    arrayOfByte1[(i2++)] = arrayOfByte3[i7];
                }
                j = 0;
            } else {
                return null;
            }
        }
        if (j != 0) {
            if (i2 + 2 > 16) {
                return null;
            }
            arrayOfByte1[(i2++)] = ((byte) (k >> 8 & 0xFF));
            arrayOfByte1[(i2++)] = ((byte) (k & 0xFF));
        }
        if (i != -1) {
            i4 = i2 - i;
            if (i2 == 16) {
                return null;
            }
            for (i1 = 1; i1 <= i4; i1++) {
                arrayOfByte1[(16 - i1)] = arrayOfByte1[(i + i4 - i1)];
                arrayOfByte1[(i + i4 - i1)] = 0;
            }
            i2 = 16;
        }
        if (i2 != 16) {
            return null;
        }
        byte[] arrayOfByte2 = convertFromIPv4MappedAddress(arrayOfByte1);
        if (arrayOfByte2 != null) {
            return arrayOfByte2;
        }
        return arrayOfByte1;
    }

    public static boolean isIPv4LiteralAddress(String paramString) {
        return textToNumericFormatV4(paramString) != null;
    }

    public static boolean isIPv6LiteralAddress(String paramString) {
        return textToNumericFormatV6(paramString) != null;
    }

    public static byte[] convertFromIPv4MappedAddress(byte[] paramArrayOfByte) {
        if (isIPv4MappedAddress(paramArrayOfByte)) {
            byte[] arrayOfByte = new byte[4];
            System.arraycopy(paramArrayOfByte, 12, arrayOfByte, 0, 4);
            return arrayOfByte;
        }
        return null;
    }

    private static boolean isIPv4MappedAddress(byte[] paramArrayOfByte) {
        if (paramArrayOfByte.length < 16) {
            return false;
        }
        return (paramArrayOfByte[0] == 0) && (paramArrayOfByte[1] == 0) && (paramArrayOfByte[2] == 0) && (paramArrayOfByte[3] == 0)
                && (paramArrayOfByte[4] == 0) && (paramArrayOfByte[5] == 0) && (paramArrayOfByte[6] == 0) && (paramArrayOfByte[7] == 0)
                && (paramArrayOfByte[8] == 0) && (paramArrayOfByte[9] == 0) && (paramArrayOfByte[10] == -1) && (paramArrayOfByte[11] == -1);
    }

    /**
     * 是否是本地地址
     */
    public static boolean isLocalAddress(String ip) {
        return StringUtils.inString(ip, "127.0.0.1", "0:0:0:0:0:0:0:1");
    }
}
