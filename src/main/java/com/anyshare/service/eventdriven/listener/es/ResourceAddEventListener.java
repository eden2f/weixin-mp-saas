package com.anyshare.service.eventdriven.listener.es;

import com.anyshare.service.eventdriven.event.ResourceAddEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @author : Eden
 * @date : 2021/4/5
 */
@Slf4j
@Component
public class ResourceAddEventListener extends BaseEventListener implements ApplicationListener<ResourceAddEvent> {

    @Override
//    @Async(value = "taskExecutor")
    public void onApplicationEvent(ResourceAddEvent resourceAddEvent) {
        super.pushContentToEs(resourceAddEvent.getId(), resourceAddEvent.getResourceType());
    }
}
