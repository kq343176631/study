package com.style.web.filter;

import com.style.utils.io.ResourceUtils;
import com.style.utils.lang.MapUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.core.annotation.Order;
import org.springframework.util.ClassUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.NestedServletException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * 错误页面过滤器
 */
@Order(-2147483647)
public class ErrorPageFilter extends OncePerRequestFilter {

    private static final Log logger = LogFactory.getLog(ErrorPageFilter.class);

    // 定义键值
    private static final String ERROR_EXCEPTION = "javax.servlet.error.exception";
    private static final String ERROR_EXCEPTION_TYPE = "javax.servlet.error.exception_type";
    private static final String ERROR_MESSAGE = "javax.servlet.error.message";
    private static final String ERROR_REQUEST_URI = "javax.servlet.error.request_uri";
    private static final String ERROR_STATUS_CODE = "javax.servlet.error.status_code";

    // 客户端异常
    private static final Set<Class<?>> CLIENT_ABORT_EXCEPTIONS;

    // 全局错误页面路径
    private String global;
    // 状态码错误页面
    private final Map<Integer, String> statusErrorPageMap = MapUtils.newHashMap();
    // 异常错误页面
    private final Map<Class<?>, String> exceptionErrorPageMap = MapUtils.newHashMap();

    static {
        Set<Class<?>> clientAbortExceptions = new HashSet<>();
        addClassIfPresent(clientAbortExceptions,
                "org.apache.catalina.connector.ClientAbortException");
        CLIENT_ABORT_EXCEPTIONS = Collections.unmodifiableSet(clientAbortExceptions);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {

        ResponseErrorWrapper wrapped = new ResponseErrorWrapper(response);

        try {
            chain.doFilter(request, wrapped);
            // 判断是否产生了错误
            if (wrapped.hasErrorToSend()) {
                this.handleErrorStatus(request, response, wrapped.getStatus(), wrapped.getMessage());
                response.flushBuffer();
            } else if (!request.isAsyncStarted() && !response.isCommitted()) {
                response.flushBuffer();
            }
        } catch (Throwable throwable) {
            Throwable exceptionToHandle = throwable;
            if (throwable instanceof NestedServletException) {
                exceptionToHandle = ((NestedServletException) throwable).getRootCause();
            }

            this.handleException(request, response, wrapped, exceptionToHandle);
            response.flushBuffer();
        }
    }

    /**
     * 处理错误状态
     *
     * @param request  request
     * @param response response
     * @param status   status
     * @param message  message
     * @throws ServletException ServletException
     * @throws IOException      IOException
     */
    private void handleErrorStatus(HttpServletRequest request, HttpServletResponse response, int status, String message) throws ServletException, IOException {
        if (response.isCommitted()) {
            this.handleCommittedResponse(request, null);
        } else {
            String errorPath = this.getErrorPath(this.statusErrorPageMap, status);
            if (errorPath == null) {
                response.sendError(status, message);
            } else {
                response.setStatus(status);
                this.setErrorAttributes(request, status, message);
                request.getRequestDispatcher(errorPath).forward(request, response);
            }
        }
    }

    /**
     * 处理异常
     *
     * @param request  request
     * @param response response
     * @param wrapped  wrapped
     * @param ex       ex
     * @throws IOException      IOException
     * @throws ServletException ServletException
     */
    private void handleException(HttpServletRequest request, HttpServletResponse response, ResponseErrorWrapper wrapped, Throwable ex) throws IOException, ServletException {
        Class<?> type = ex.getClass();
        String errorPath = this.getErrorPath(type);
        if (errorPath == null) {
            this.rethrow(ex);
        } else if (response.isCommitted()) {
            this.handleCommittedResponse(request, ex);
        } else {
            this.forwardToErrorPage(errorPath, request, wrapped, ex);
        }
    }

    /**
     * 处理已经提交的 Response
     *
     * @param request request
     * @param ex      ex
     */
    private void handleCommittedResponse(HttpServletRequest request, Throwable ex) {
        if (!this.isClientAbortException(ex)) {
            String message = "Cannot forward to error page for request " + this.getDescription(request) + " as the response has already been committed. As a result, the response may have the wrong status code. If your application is running on WebSphere Application Server you may be able to resolve this problem by setting com.ibm.ws.webcontainer.invokeFlushAfterService to false";
            if (ex == null) {
                logger.error(message);
            } else {
                logger.error(message, ex);
            }
        }
    }

    /**
     * 转发错误页面
     *
     * @param path     path
     * @param request  request
     * @param response response
     * @param ex       ex
     * @throws ServletException ServletException
     * @throws IOException      IOException
     */
    private void forwardToErrorPage(String path, HttpServletRequest request, HttpServletResponse response, Throwable ex) throws ServletException, IOException {
        if (logger.isErrorEnabled()) {
            String message = "Forwarding to error page from request " + this.getDescription(request) + " due to exception [" + ex.getMessage() + "]";
            logger.error(message, ex);
        }

        this.setErrorAttributes(request, 500, ex.getMessage());
        request.setAttribute(ERROR_EXCEPTION, ex);
        request.setAttribute(ERROR_EXCEPTION_TYPE, ex.getClass());
        response.reset();
        response.setStatus(500);

        request.getRequestDispatcher(path).forward(request, response);

        request.removeAttribute(ERROR_EXCEPTION);
        request.removeAttribute(ERROR_EXCEPTION_TYPE);
    }

    /**
     * 判断该异常是否由客户端引起的
     *
     * @param ex ex
     * @return boolean
     */
    private boolean isClientAbortException(Throwable ex) {
        if (ex == null) {
            return false;
        } else {
            Iterator iterator = CLIENT_ABORT_EXCEPTIONS.iterator();

            Class candidate;
            do {
                if (!iterator.hasNext()) {
                    return this.isClientAbortException(ex.getCause());
                }

                candidate = (Class) iterator.next();
            } while (!candidate.isInstance(ex));

            return true;
        }
    }

    /**
     * 从请求中获取描述信息
     *
     * @param request request
     * @return Description
     */
    private String getDescription(HttpServletRequest request) {
        String pathInfo = request.getPathInfo() != null ? request.getPathInfo() : "";
        return "[" + request.getServletPath() + pathInfo + "]";
    }

    /**
     * 向集合中添加CLASS
     *
     * @param collection collection
     * @param className  className
     */
    private static void addClassIfPresent(Collection<Class<?>> collection, String className) {
        try {
            collection.add(ClassUtils.forName(className, ResourceUtils.getClassLoader()));
        } catch (Throwable throwable) {
            //
        }
    }

    // 添加错误页面
    public void addErrorPages(List<ErrorPage> errorPages) {
        for (ErrorPage errorPage : errorPages) {
            if (errorPage.isGlobal()) {
                this.global = errorPage.getPath();
            } else if (errorPage.getStatus() != null) {
                this.statusErrorPageMap.put(errorPage.getStatus().value(), errorPage.getPath());
            } else {
                this.exceptionErrorPageMap.put(errorPage.getException(), errorPage.getPath());
            }
        }
    }

    /**
     * 重新抛出异常
     *
     * @param ex ex
     * @throws IOException      IOException
     * @throws ServletException ServletException
     */
    private void rethrow(Throwable ex) throws IOException, ServletException {
        if (ex instanceof RuntimeException) {
            throw (RuntimeException) ex;
        } else if (ex instanceof Error) {
            throw (Error) ex;
        } else if (ex instanceof IOException) {
            throw (IOException) ex;
        } else if (ex instanceof ServletException) {
            throw (ServletException) ex;
        } else {
            throw new IllegalStateException(ex);
        }
    }

    /**
     * 通过异常类对象查询错误页面路径
     *
     * @param exClass exClass
     * @return path
     */
    private String getErrorPath(Class<?> exClass) {
        while (exClass != Object.class) {
            String path = this.exceptionErrorPageMap.get(exClass);
            if (path != null) {
                return path;
            }

            exClass = exClass.getSuperclass();
        }

        return this.global;
    }

    /**
     * 通过状态码获取错误页面路径
     *
     * @param map    map
     * @param status status
     * @return path
     */
    private String getErrorPath(Map<Integer, String> map, Integer status) {
        return map.containsKey(status) ? map.get(status) : this.global;
    }

    /**
     * 在Request中保存错误信息
     *
     * @param request request
     * @param status  status
     * @param message message
     */
    private void setErrorAttributes(HttpServletRequest request, int status, String message) {
        request.setAttribute(ERROR_STATUS_CODE, status);
        request.setAttribute(ERROR_MESSAGE, message);
        request.setAttribute(ERROR_REQUEST_URI, request.getRequestURI());
    }

    @Override
    public void destroy() {
    }

    /**
     * ErrorWrapperResponse
     */
    private static class ResponseErrorWrapper extends HttpServletResponseWrapper {
        private int status;
        private String message;
        private boolean hasErrorToSend = false;

        ResponseErrorWrapper(HttpServletResponse response) {
            super(response);
        }

        /**
         * 该方法在运行出错后调用
         *
         * @param status status
         * @throws IOException IOException
         */
        @Override
        public void sendError(int status) throws IOException {
            this.sendError(status, null);
        }

        /**
         * 该方法在运行出错后调用
         * 将错误的状态码封装到 status message
         *
         * @param status  status
         * @param message message
         */
        @Override
        public void sendError(int status, String message) {
            this.status = status;
            this.message = message;
            this.hasErrorToSend = true;
        }

        @Override
        public int getStatus() {
            return this.hasErrorToSend ? this.status : super.getStatus();
        }

        @Override
        public void flushBuffer() throws IOException {
            this.sendErrorIfNecessary();
            super.flushBuffer();
        }

        @Override
        public PrintWriter getWriter() throws IOException {
            this.sendErrorIfNecessary();
            return super.getWriter();
        }

        @Override
        public ServletOutputStream getOutputStream() throws IOException {
            this.sendErrorIfNecessary();
            return super.getOutputStream();
        }

        private void sendErrorIfNecessary() throws IOException {
            if (this.hasErrorToSend && !this.isCommitted()) {
                ((HttpServletResponse) this.getResponse()).sendError(this.status, this.message);
            }

        }

        public String getMessage() {
            return this.message;
        }

        public boolean hasErrorToSend() {
            return this.hasErrorToSend;
        }
    }
}
