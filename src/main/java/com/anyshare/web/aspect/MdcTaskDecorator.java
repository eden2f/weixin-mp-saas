package com.anyshare.web.aspect;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.core.task.TaskDecorator;

import java.util.Map;
import java.util.UUID;

import static com.anyshare.web.aspect.TraceIdFilter.TRACE_ID;

/**
 * @author Eden
 * @date 2020/07/25
 */
public class MdcTaskDecorator implements TaskDecorator {
    @Override
    public Runnable decorate(Runnable runnable) {
        Map<String, String> map = MDC.getCopyOfContextMap();
        return () -> {
            try {
                MDC.setContextMap(map);
                String traceId = MDC.get(TRACE_ID);
                if (StringUtils.isBlank(traceId)) {
                    traceId = UUID.randomUUID().toString();
                    MDC.put(TRACE_ID, traceId);
                }
                runnable.run();
            } finally {
                MDC.clear();
            }
        };
    }
}
