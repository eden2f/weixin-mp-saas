package com.anyshare.service.common;

import com.anyshare.jpa.po.ShareResourcePO;
import com.anyshare.jpa.repository.ShareResourceRepository;
import lombok.extern.slf4j.Slf4j;
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

    @Override
    public List<ShareResourcePO> findTop6ByNameContaining(String appTag, String name) {
        return shareResourceRepository.findTop6ByAppTagAndNameContaining(appTag, name);
    }

    @Override
    public Long insert(String appTag, ShareResourcePO shareResource) {
        shareResource.setAppTag(appTag);
        Optional<ShareResourcePO> optional = shareResourceRepository.findTopByAppTagAndName(appTag, shareResource.getName());
        if (optional.isPresent()) {
            return optional.get().getId();
        }
        shareResourceRepository.save(shareResource);
        return shareResource.getId();
    }
}
