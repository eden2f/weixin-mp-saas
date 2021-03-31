package com.anyshare.service.common;

import com.anyshare.jpa.po.ShareResourcePO;

import java.util.List;

/**
 * @author Eden
 * @date 2020/07/25
 */
public interface ShareResourceService {

    List<ShareResourcePO> findTop6ByNameContaining(String appTag, String keyword);

    Long insert(String appTag, ShareResourcePO shareResource);
}
