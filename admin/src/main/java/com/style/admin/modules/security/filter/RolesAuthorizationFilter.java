package com.style.admin.modules.security.filter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

/**
 * 角色权限过滤器
 * @author ThinkGem
 * @version 2017-03-22
 */
public class RolesAuthorizationFilter extends org.apache.shiro.web.filter.authz.RolesAuthorizationFilter {

	@Override
	protected void redirectToLogin(ServletRequest request, ServletResponse response) throws IOException {
		PermissionsAuthorizationFilter.redirectToDefaultPath(request, response);
	}
	
	@Override
	protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws IOException {
		return PermissionsAuthorizationFilter.redirectTo403Page(request, response);
    }
}
