package com.anyshare.service.common;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.crypto.digest.MD5;
import com.anyshare.jpa.po.WxMpNewsArticlePO;
import com.anyshare.jpa.repository.WxMpNewsArticleRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
public class WxMpNewsArticleServiceImpl implements WxMpNewsArticleService {

    @Resource
    private WxMpNewsArticleRepository wxMpNewsArticleRepository;

    @Override
    public List<WxMpNewsArticlePO> findTop6ByTitleContaining(String appTag, String title) {
        return wxMpNewsArticleRepository.findTop6ByAppTagAndTitleContaining(appTag, title);
    }

    @Override
    public Optional<WxMpNewsArticlePO> findTopByTitle(String appTag, String title) {
        return wxMpNewsArticleRepository.findTopByAppTagAndTitle(appTag, title);
    }

    @Override
    public Long insert(String appTag, WxMpNewsArticlePO wxMpNewsArticle) {
        String contentMd5 = MD5.create().digestHex16(wxMpNewsArticle.getContent());
        Optional<WxMpNewsArticlePO> wxMpNewsArticleOptional = findTopByTitle(appTag, wxMpNewsArticle.getTitle());
        if (wxMpNewsArticleOptional.isPresent()) {
            WxMpNewsArticlePO wxMpNewsArticleInDb = wxMpNewsArticleOptional.get();
            String contentMd5InDb = wxMpNewsArticleInDb.getContentMd5();
            if (StringUtils.isNotBlank(contentMd5InDb) && contentMd5InDb.equals(contentMd5)) {
                return wxMpNewsArticleInDb.getId();
            }
            BeanUtil.copyProperties(wxMpNewsArticle, wxMpNewsArticleInDb, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));
            wxMpNewsArticle = wxMpNewsArticleInDb;
        }
        wxMpNewsArticle.setAppTag(appTag);
        wxMpNewsArticle.setContentMd5(contentMd5);
        wxMpNewsArticleRepository.save(wxMpNewsArticle);
        return wxMpNewsArticle.getId();
    }
}
