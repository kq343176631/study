package com.style.common.codec;

import com.style.common.lang.StringUtils;

/**
 * DES加密解密工具（可逆）
 * 加密：DesUtils.encode("admin","1,2,3");
 * 解密：DesUtils.decode("012C2C9BA925FAF8045B2FD9B02A2664","1,2,3");
 */
public class DesUtils {

    private static DesCore desCore = new DesCore();

    /**
     * DES加密（secretKey代表3个key，用逗号分隔）
     */
    public static String encode(String data, String secretKey) {
        if (StringUtils.isBlank(data)) {
            return "";
        }
        String[] ks = StringUtils.split(secretKey, ",");
        if (ks.length >= 3) {
            return desCore.strEnc(data, ks[0], ks[1], ks[2]);
        }
        return desCore.strEnc(data, secretKey, "", "");
    }

    /**
     * DES解密（secretKey代表3个key，用逗号分隔）
     */
    public static String decode(String data, String secretKey) {
        if (StringUtils.isBlank(data)) {
            return "";
        }
        String[] ks = StringUtils.split(secretKey, ",");
        if (ks.length >= 3) {
            return desCore.strDec(data, ks[0], ks[1], ks[2]);
        }
        return desCore.strDec(data, secretKey, "", "");
    }
}
