package com.anyshare.web.aspect;

import com.anyshare.web.utils.HttpLoggingUtil;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * This is Description
 *
 * @author Eden
 * @date 2020/07/19
 */
@Component
@Slf4j
@WebFilter(filterName = "logFilter", urlPatterns = "/*")
public class LogFilter implements Filter {

    public static final List<String> LOG_FILTER_IGNORE_URL_PATTERNS = Collections.singletonList("/weixin/");


    @Override
    public void init(FilterConfig filterConfig) {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper((HttpServletRequest) request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper((HttpServletResponse) response);
        ObjectNode rootNode = HttpLoggingUtil.initByHttpServletRequest(requestWrapper);

        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String uri = httpServletRequest.getRequestURI();
        for (String logFilterIgnoreUrlPattern : LOG_FILTER_IGNORE_URL_PATTERNS) {
            if (uri.contains(logFilterIgnoreUrlPattern)) {
                chain.doFilter(request, response);
                HttpLoggingUtil.updateByHttpXmlServletResponse(rootNode, requestWrapper, responseWrapper);
                log.info(rootNode.toString());
                return;
            }
        }
        chain.doFilter(requestWrapper, responseWrapper);
        HttpLoggingUtil.updateByHttpJsonServletResponse(rootNode, requestWrapper, responseWrapper);
        log.info(rootNode.toString());
    }

    @Override
    public void destroy() {

    }
}

