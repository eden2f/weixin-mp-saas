package com.anyshare.jpa.mysql.repository;

import com.anyshare.jpa.mysql.po.WxMpNewsArticlePO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    Page<WxMpNewsArticlePO> findPageBydelStatus(int delStatus, Pageable pageable);
}