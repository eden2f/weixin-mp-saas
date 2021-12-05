package com.anyshare.service.common;

import com.anyshare.jpa.mysql.po.WxMpNewsArticlePO;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

/**
 * @author Eden
 * @date 2020/07/25
 */
public interface WxMpNewsArticleService {

    List<WxMpNewsArticlePO> findTop6ByTitleContaining(String appTag, String keyword);

    Optional<WxMpNewsArticlePO> findTopByTitle(String appTag, String title);

    Long insert(String appTag, WxMpNewsArticlePO wxMpNewsArticlePo);

    Page<WxMpNewsArticlePO> page(int pageNum, int pageSize);

    List<WxMpNewsArticlePO> findByIds(List<Long> weixinArticleIds);
}
