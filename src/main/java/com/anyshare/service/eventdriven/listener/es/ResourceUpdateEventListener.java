package com.anyshare.service.eventdriven.listener.es;

import com.anyshare.service.eventdriven.event.ResourceUpdateEvent;
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
public class ResourceUpdateEventListener extends BaseEventListener implements ApplicationListener<ResourceUpdateEvent> {

    @Override
//    @Async(value = "taskExecutor")
    public void onApplicationEvent(ResourceUpdateEvent resourceUpdateEvent) {
        super.pushContentToEs(resourceUpdateEvent.getId(), resourceUpdateEvent.getResourceType());
    }
}
