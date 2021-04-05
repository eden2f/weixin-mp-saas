package com.anyshare.service.eventdriven.event;

import com.anyshare.enums.ResourceType;
import lombok.*;
import org.springframework.context.ApplicationEvent;

/**
 * @author : Eden
 * @date : 2021/4/5
 */
@Getter
@Setter
@ToString
public class ResourceAddEvent extends ApplicationEvent {

    private Long id;

    private ResourceType resourceType;

    public ResourceAddEvent(Long id, ResourceType resourceType) {
        super(String.format("%s_%s", id, resourceType));
        this.id = id;
        this.resourceType = resourceType;
    }
}
