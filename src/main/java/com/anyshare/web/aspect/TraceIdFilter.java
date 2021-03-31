package com.anyshare.web.aspect;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;
import java.util.UUID;

/**
 * @author Eden
 * @date 2020/07/25
 */
@Slf4j
@WebFilter(filterName = "traceIdFilter", urlPatterns = "/*")
@Order(0)
@Component
public class TraceIdFilter implements Filter {
    /**
     * 日志跟踪标识
     */
    public static final String TRACE_ID = "TRACE_ID";

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        MDC.put(TRACE_ID, UUID.randomUUID().toString());
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {
        MDC.clear();
    }
}
