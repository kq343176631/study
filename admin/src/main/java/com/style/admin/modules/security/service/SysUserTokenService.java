/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package com.style.admin.modules.security.service;

import com.style.admin.modules.security.entity.SysUserTokenEntity;
import com.style.common.model.Result;
import com.style.common.service.BaseService;
import com.style.common.service.CrudService;

/**
 * 用户Token
 * 
 * @author Mark sunlightcs@gmail.com
 */
public interface SysUserTokenService extends CrudService<SysUserTokenEntity> {

	/**
	 * 生成token
	 * @param userId  用户ID
	 */
	Result createToken(Long userId);

	/**
	 * 退出，修改token值
	 * @param userId  用户ID
	 */
	void logout(Long userId);

}