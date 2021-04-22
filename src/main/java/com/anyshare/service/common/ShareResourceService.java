package com.anyshare.service.common;

import com.anyshare.jpa.mysql.po.ShareResourcePO;

import java.util.List;
import java.util.Optional;

/**
 * @author Eden
 * @date 2020/07/25
 */
public interface ShareResourceService {

    List<ShareResourcePO> findTop6ByNameContaining(String appTag, String name);

    Optional<ShareResourcePO> findTopByAppTagAndName(String appTag, String name);

    Long insert(String appTag, ShareResourcePO shareResource);
}
