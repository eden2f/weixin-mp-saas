package com.anyshare.service.common;

import com.anyshare.jpa.po.ShareResourcePO;
import com.anyshare.jpa.repository.ShareResourceRepository;
import com.anyshare.enums.ResourceType;
import com.anyshare.service.eventdriven.event.ResourceAddEvent;
import com.anyshare.service.eventdriven.event.ResourceUpdateEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

/**
 * @author Eden
 * @date 2020/07/25
 */
@Slf4j
@Service
public class ShareResourceServiceImpl implements ShareResourceService {

    @Resource
    private ShareResourceRepository shareResourceRepository;
    @Resource
    private ApplicationContext applicationContext;

    @Override
    public List<ShareResourcePO> findTop6ByNameContaining(String appTag, String name) {
        return shareResourceRepository.findTop6ByAppTagAndNameContaining(appTag, name);
    }

    @Override
    public Long insert(String appTag, ShareResourcePO shareResource) {
        shareResource.setAppTag(appTag);
        Optional<ShareResourcePO> optional = shareResourceRepository.findTopByAppTagAndName(appTag, shareResource.getName());
        ApplicationEvent applicationEvent = null;
        if (optional.isPresent()) {
            ShareResourcePO shareResourcePo = optional.get();
            BeanUtils.copyProperties(shareResource, shareResourcePo);
            shareResource = shareResourcePo;
            applicationEvent = new ResourceUpdateEvent(shareResource.getId(), ResourceType.SHARE_RESOURCE);
        }
        shareResourceRepository.save(shareResource);
        if(applicationEvent == null){
            applicationEvent = new ResourceAddEvent(shareResource.getId(), ResourceType.SHARE_RESOURCE);
        }
        applicationContext.publishEvent(applicationEvent);
        return shareResource.getId();
    }
}
