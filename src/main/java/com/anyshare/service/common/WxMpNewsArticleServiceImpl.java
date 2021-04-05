package com.anyshare.service.common;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.crypto.digest.MD5;
import com.anyshare.jpa.po.WxMpNewsArticlePO;
import com.anyshare.jpa.repository.WxMpNewsArticleRepository;
import com.anyshare.enums.ResourceType;
import com.anyshare.service.eventdriven.event.ResourceAddEvent;
import com.anyshare.service.eventdriven.event.ResourceUpdateEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
public class WxMpNewsArticleServiceImpl implements WxMpNewsArticleService {

    @Resource
    private WxMpNewsArticleRepository wxMpNewsArticleRepository;
    @Resource
    private ApplicationContext applicationContext;

    @Override
    public List<WxMpNewsArticlePO> findTop6ByTitleContaining(String appTag, String title) {
        return wxMpNewsArticleRepository.findTop6ByAppTagAndTitleContaining(appTag, title);
    }

    @Override
    public Optional<WxMpNewsArticlePO> findTopByTitle(String appTag, String title) {
        return wxMpNewsArticleRepository.findTopByAppTagAndTitle(appTag, title);
    }

    @Override
    public Long insert(String appTag, WxMpNewsArticlePO wxMpNewsArticlePo) {
        String contentMd5 = MD5.create().digestHex16(wxMpNewsArticlePo.getContent());
        Optional<WxMpNewsArticlePO> wxMpNewsArticleOptional = findTopByTitle(appTag, wxMpNewsArticlePo.getTitle());
        ApplicationEvent applicationEvent = null;
        if (wxMpNewsArticleOptional.isPresent()) {
            WxMpNewsArticlePO wxMpNewsArticleInDb = wxMpNewsArticleOptional.get();
            String contentMd5InDb = wxMpNewsArticleInDb.getContentMd5();
            if (StringUtils.isNotBlank(contentMd5InDb) && contentMd5InDb.equals(contentMd5)) {
                return wxMpNewsArticleInDb.getId();
            }
            BeanUtil.copyProperties(wxMpNewsArticlePo, wxMpNewsArticleInDb, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));
            wxMpNewsArticlePo = wxMpNewsArticleInDb;
            applicationEvent = new ResourceUpdateEvent(wxMpNewsArticleInDb.getId(), ResourceType.WEIXIN_ARTICLE);
        }
        wxMpNewsArticlePo.setAppTag(appTag);
        wxMpNewsArticlePo.setContentMd5(contentMd5);
        wxMpNewsArticleRepository.save(wxMpNewsArticlePo);
        if(applicationEvent == null){
            applicationEvent = new ResourceAddEvent(wxMpNewsArticlePo.getId(), ResourceType.WEIXIN_ARTICLE);
        }
        applicationContext.publishEvent(applicationEvent);
        return wxMpNewsArticlePo.getId();
    }
}
