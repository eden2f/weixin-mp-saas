package com.anyshare.jpa.repository;

import com.anyshare.jpa.po.WxMpNewsArticlePO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * @author Eden
 * @date 2020/07/25
 */
public interface WxMpNewsArticleRepository extends JpaRepository<WxMpNewsArticlePO, Long> {

    List<WxMpNewsArticlePO> findTop6ByAppTagAndTitleContaining(String appTag, String title);

    Optional<WxMpNewsArticlePO> findTopByAppTagAndTitle(String appTag, String title);
}