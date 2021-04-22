package com.anyshare.service.eventdriven.event;

import com.anyshare.enums.ResourceType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.context.ApplicationEvent;

import javax.validation.constraints.NotNull;

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

    public ResourceAddEvent(@NotNull Long id, @NotNull ResourceType resourceType) {
        super(String.format("%s_%s", id, resourceType));
        this.id = id;
        this.resourceType = resourceType;
    }
}
