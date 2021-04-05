package com.anyshare.service.eventdriven.event;

import com.anyshare.enums.ResourceType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.context.ApplicationEvent;

/**
 * @author : Eden
 * @date : 2021/4/5
 */
@Getter
@Setter
@ToString
public class ResourceUpdateEvent extends ApplicationEvent {

    private Long id;

    private ResourceType resourceType;

    public ResourceUpdateEvent(Long id, ResourceType resourceType) {
        super(String.format("%s_%s", id, resourceType));
        this.id = id;
        this.resourceType = resourceType;
    }
}
