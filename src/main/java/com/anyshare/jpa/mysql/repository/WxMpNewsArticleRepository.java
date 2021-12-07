package com.anyshare.jpa.mysql.repository;

import com.anyshare.jpa.mysql.po.WxMpNewsArticlePO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    @Query(value = "select id, url from WxMpNewsArticlePO where id in (:weixinArticleIds)")
    List<Object[]> findIdAndUrlById(@Param("weixinArticleIds") List<Long> weixinArticleIds);
}