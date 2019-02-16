package com.style.web.http;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class GetHttpServletRequestWrapper extends HttpServletRequestWrapper {

    public GetHttpServletRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    public GetHttpServletRequestWrapper(ServletRequest request) {
        super((HttpServletRequest) request);
    }

    @Override
    public String getMethod() {
        return "";
    }
}
