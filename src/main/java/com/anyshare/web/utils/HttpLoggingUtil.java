package com.anyshare.web.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * This is Description
 *
 * @author Eden
 * @date 2020/07/25
 */
public class HttpLoggingUtil {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static ObjectNode initByHttpServletRequest(ContentCachingRequestWrapper requestWrapper) {
        ObjectNode rootNode = MAPPER.createObjectNode();
        rootNode.put("costTime", System.currentTimeMillis());
        rootNode.put("uri", requestWrapper.getRequestURI());
        rootNode.put("clientIp", requestWrapper.getRemoteAddr());
        rootNode.set("requestHeaders", MAPPER.valueToTree(getRequestHeaders(requestWrapper)));
        return rootNode;
    }

    /**
     * todo updateByHttpXmlServletResponse
     */
    public static void updateByHttpXmlServletResponse(ObjectNode rootNode, ContentCachingRequestWrapper requestWrapper, ContentCachingResponseWrapper responseWrapper) throws IOException {
        String method = requestWrapper.getMethod();
        rootNode.put("method", method);
        rootNode.set("request", null);
        rootNode.put("status", responseWrapper.getStatus());
        rootNode.set("response", null);
        responseWrapper.copyBodyToResponse();
        rootNode.set("responseHeaders", MAPPER.valueToTree(getResponsetHeaders(responseWrapper)));
        rootNode.put("costTime", System.currentTimeMillis() - rootNode.get("costTime").asLong() + "ms");
    }

    public static void updateByHttpJsonServletResponse(ObjectNode rootNode, ContentCachingRequestWrapper requestWrapper, ContentCachingResponseWrapper responseWrapper) throws IOException {
        String method = requestWrapper.getMethod();
        rootNode.put("method", method);
        if (method.equals(RequestMethod.GET.name())) {
            rootNode.set("request", MAPPER.valueToTree(requestWrapper.getParameterMap()));
        } else {
            JsonNode newNode = MAPPER.readTree(requestWrapper.getContentAsByteArray());
            rootNode.set("request", newNode);
        }
        rootNode.put("status", responseWrapper.getStatus());
        JsonNode newNode = MAPPER.readTree(responseWrapper.getContentAsByteArray());
        rootNode.set("response", newNode);
        responseWrapper.copyBodyToResponse();
        rootNode.set("responseHeaders", MAPPER.valueToTree(getResponsetHeaders(responseWrapper)));
        rootNode.put("costTime", System.currentTimeMillis() - rootNode.get("costTime").asLong() + "ms");
    }

    private static Map<String, Object> getResponsetHeaders(ContentCachingResponseWrapper response) {
        Map<String, Object> headers = new HashMap<>();
        Collection<String> headerNames = response.getHeaderNames();
        for (String headerName : headerNames) {
            headers.put(headerName, response.getHeader(headerName));
        }
        return headers;
    }

    private static Map<String, Object> getRequestHeaders(HttpServletRequest request) {
        Map<String, Object> headers = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            headers.put(headerName, request.getHeader(headerName));
        }
        return headers;
    }
}
