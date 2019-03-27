/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package com.style.admin.modules.security.service;

import java.awt.image.BufferedImage;

/**
 * 验证码
 */
public interface CaptchaService {

    /**
     * 图片验证码
     */
    BufferedImage create(String uuid);

    /**
     * 验证码效验
     * @param uuid  uuid
     * @param code  验证码
     * @return  true：成功  false：失败
     */
    boolean validate(String uuid, String code);
}
