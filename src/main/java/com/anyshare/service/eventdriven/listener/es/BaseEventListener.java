package com.anyshare.service.eventdriven.listener.es;

import com.anyshare.enums.ResourceType;
import com.anyshare.jpa.es.po.SearchContentPO;
import com.anyshare.jpa.mysql.po.ShareResourcePO;
import com.anyshare.jpa.mysql.po.WxMpNewsArticlePO;
import com.anyshare.jpa.mysql.repository.ShareResourceRepository;
import com.anyshare.jpa.mysql.repository.WxMpNewsArticleRepository;
import com.anyshare.service.SearchContentService;
import com.anyshare.web.utils.MarkdownUtil;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.util.Optional;

/**
 * @author : Eden
 * @date : 2021/4/5
 */
@Slf4j
public abstract class BaseEventListener {

    @Resource
    private WxMpNewsArticleRepository wxMpNewsArticleRepository;
    @Resource
    private ShareResourceRepository shareResourceRepository;
    @Resource
    private SearchContentService searchContentService;

    protected void pushContentToEs(Long id, ResourceType resourceType) {
        log.info("推送数据到ES, id = {}, resourceType = {}", id, resourceType);
        switch (resourceType) {
            case SHARE_RESOURCE:
                Optional<ShareResourcePO> resourceOptional = shareResourceRepository.findById(id);
                if (resourceOptional.isPresent()) {
                    ShareResourcePO shareResource = resourceOptional.get();
                    SearchContentPO searchContent = new SearchContentPO();
                    searchContent.setAppTag(shareResource.getAppTag());
                    searchContent.setOriginalId(shareResource.getId());
                    searchContent.setTitle(shareResource.getName());
                    searchContent.setDigest(shareResource.getName());
                    searchContent.setContent(shareResource.getContent());
                    searchContent.setResourceType(resourceType.getCode());
                    searchContentService.saveOrUpdate(searchContent);
                }
                break;
            case WEIXIN_ARTICLE:
                Optional<WxMpNewsArticlePO> articleOptional = wxMpNewsArticleRepository.findById(id);
                if (articleOptional.isPresent()) {
                    WxMpNewsArticlePO wxMpNewsArticle = articleOptional.get();
                    SearchContentPO searchContent = new SearchContentPO();
                    searchContent.setAppTag(wxMpNewsArticle.getAppTag());
                    searchContent.setOriginalId(wxMpNewsArticle.getId());
                    searchContent.setTitle(wxMpNewsArticle.getTitle());
                    searchContent.setDigest(wxMpNewsArticle.getDigest());
                    searchContent.setContent(MarkdownUtil.html2PlainText(wxMpNewsArticle.getContent()));
                    searchContent.setResourceType(resourceType.getCode());
                    searchContentService.saveOrUpdate(searchContent);
                }
                break;
            default:
                break;
        }
    }
}
