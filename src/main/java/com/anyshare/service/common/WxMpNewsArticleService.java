package com.anyshare.service.common;

import com.anyshare.jpa.po.WxMpNewsArticlePO;

import java.util.List;
import java.util.Optional;

/**
 * @author Eden
 * @date 2020/07/25
 */
public interface WxMpNewsArticleService {

    List<WxMpNewsArticlePO> findTop6ByTitleContaining(String appTag, String keyword);

    Optional<WxMpNewsArticlePO> findTopByTitle(String appTag, String title);

    Long insert(String appTag, WxMpNewsArticlePO wxMpNewsArticlePO);
}
