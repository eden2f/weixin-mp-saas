package com.anyshare.service;

import com.anyshare.enums.AppTag;
import com.anyshare.enums.ResourceType;
import com.anyshare.exception.ServiceException;
import com.anyshare.jpa.es.po.SearchContentPO;
import com.anyshare.jpa.mysql.po.AppOpenApiConfigPO;
import com.anyshare.jpa.mysql.po.BasePO;
import com.anyshare.jpa.mysql.po.ShareResourcePO;
import com.anyshare.jpa.mysql.po.WxMpNewsArticlePO;
import com.anyshare.service.common.AppOpenApiConfigService;
import com.anyshare.service.common.ShareResourceService;
import com.anyshare.service.common.WxMpNewsArticleService;
import com.anyshare.service.eventdriven.event.ResourceUpdateEvent;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpFreePublishService;
import me.chanjar.weixin.mp.api.WxMpMaterialService;
import me.chanjar.weixin.mp.bean.freepublish.WxMpFreePublishArticles;
import me.chanjar.weixin.mp.bean.freepublish.WxMpFreePublishItem;
import me.chanjar.weixin.mp.bean.freepublish.WxMpFreePublishList;
import me.chanjar.weixin.mp.bean.material.WxMpMaterialCountResult;
import me.chanjar.weixin.mp.bean.material.WxMpMaterialNewsBatchGetResult;
import me.chanjar.weixin.mp.bean.material.WxMpNewsArticle;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutNewsMessage;
import me.chanjar.weixin.mp.builder.outxml.BaseBuilder;
import me.chanjar.weixin.mp.builder.outxml.NewsBuilder;
import me.chanjar.weixin.mp.builder.outxml.TextBuilder;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * @author Eden
 * @date 2020/07/25
 */
@Slf4j
@Service
public class WeixinServiceImpl implements WeixinService {

    @Resource
    private ShareResourceService shareResourceService;
    @Resource
    private WxMpNewsArticleService wxMpNewsArticleService;
    @Resource
    private SearchContentService searchContentService;
    @Resource
    private ApplicationContext applicationContext;
    @Resource
    private AppOpenApiConfigService appOpenApiConfigService;

    private ReentrantLock reindexEsContentLock = new ReentrantLock();

    private final static DecimalFormat scoreDecimalFormat = new DecimalFormat(".00");

    @Override
    public WxMpXmlOutMessage handle(String appTag, WxMpXmlMessage inMessage) {
        log.info("appTag = {}, inMessage = {}", appTag, inMessage);
        String keyword = inMessage.getContent().trim();
        Optional<Object> preciseQueryOptional = preciseQueryByKeyword(appTag, keyword);
        BaseBuilder wxMpXmlOutMessageBuilder;
        if (preciseQueryOptional.isPresent()) {
            wxMpXmlOutMessageBuilder = getWxMpXmlOutMessageBuilder(keyword, preciseQueryOptional.get());
        } else {
            Map<Long, String> weixinArticleIdToUrlMap = null;
            List<SearchHit<SearchContentPO>> searchHits = searchByKeyword(appTag, keyword);
            if (CollectionUtils.isNotEmpty(searchHits)) {
                List<Long> weixinArticleIds = searchHits.stream().map(SearchHit::getContent)
                        .filter(item -> item.getResourceType().equals(ResourceType.WEIXIN_ARTICLE.getCode()))
                        .map(SearchContentPO::getOriginalId).distinct().collect(Collectors.toList());
                List<WxMpNewsArticlePO> wxMpNewsArticles = wxMpNewsArticleService.findIdAndUrlById(weixinArticleIds);
                weixinArticleIdToUrlMap = wxMpNewsArticles.stream().collect(Collectors.toMap(BasePO::getId, WxMpNewsArticlePO::getUrl));
            }
            wxMpXmlOutMessageBuilder = getWxMpXmlOutMessageBuilder(keyword, searchHits, weixinArticleIdToUrlMap);
        }
        wxMpXmlOutMessageBuilder.fromUser(inMessage.getToUser());
        wxMpXmlOutMessageBuilder.toUser(inMessage.getFromUser());
        WxMpXmlOutMessage outMessage = (WxMpXmlOutMessage) wxMpXmlOutMessageBuilder.build();
        log.info("outMessage = {}", outMessage);
        return outMessage;
    }

    /**
     * es全文检索
     *
     * @param appTag  应用标识
     * @param keyword 关键词
     * @return 检索到的内容
     */
    private List<SearchHit<SearchContentPO>> searchByKeyword(String appTag, String keyword) {
        AppOpenApiConfigPO appOpenApiConfigPo = appOpenApiConfigService.findByAppTag(appTag);
        //  开启公众号分流能力的公众号启用分流查询
        SearchHits<SearchContentPO> searchHits;
        if (appOpenApiConfigPo.drainageEnable()) {
            List<AppOpenApiConfigPO> appOpenApiConfigPos = appOpenApiConfigService.findAll();
            List<String> appTags = appOpenApiConfigPos.stream().filter(AppOpenApiConfigPO::drainageEnable).map(AppOpenApiConfigPO::getAppTag).distinct().collect(Collectors.toList());
            appTags.removeIf(item -> item.equals(AppTag.Test.getCode()));
            searchHits = searchContentService.findByAppTagsOrTitleOrDigestOrContent(appTags, keyword);
        } else {
            searchHits = searchContentService.findByTitleOrDigestOrContent(appTag, keyword);
        }
        return searchHits.getSearchHits();
    }

    /**
     * 精确查询
     *
     * @param appTag  应用标识
     * @param keyword 关键词
     * @return 精确匹配到的内容
     */
    @SuppressWarnings("unchecked")
    private Optional<Object> preciseQueryByKeyword(String appTag, String keyword) {
        Optional<ShareResourcePO> shareResourceOptional = shareResourceService.findTopByAppTagAndName(appTag, keyword);
        if (shareResourceOptional.isPresent()) {
            return Optional.of(shareResourceOptional.get());
        }
        Optional<WxMpNewsArticlePO> wxMpNewsArticleOptional = wxMpNewsArticleService.findTopByTitle(appTag, keyword);
        if (wxMpNewsArticleOptional.isPresent()) {
            WxMpNewsArticlePO wxMpNewsArticle = wxMpNewsArticleOptional.get();
            return Optional.of(wxMpNewsArticle);
        } else {
            return Optional.empty();
        }
    }


    private BaseBuilder getWxMpXmlOutMessageBuilder(String keyword, @NotNull Object object) {
        BaseBuilder builder;
        if (object instanceof ShareResourcePO) {
            ShareResourcePO shareResource = (ShareResourcePO) object;
            //  关键词 ： %s
            //  —— —— —— —— —— ——
            //  资源 : %s
            //  —— —— —— —— —— ——
            //  %s
            String format = "关键词 ： %s\n" +
                    "—— —— —— —— —— ——\n" +
                    "资源 : %s\n" +
                    "—— —— —— —— —— ——\n" +
                    "%s";
            String resultMsg = String.format(format, keyword, shareResource.getName(), shareResource.getContent());
            TextBuilder textBuilder = WxMpXmlOutMessage.TEXT();
            textBuilder.content(resultMsg);
            builder = textBuilder;
        } else if (object instanceof WxMpNewsArticlePO) {
            WxMpNewsArticlePO wxMpNewsArticle = (WxMpNewsArticlePO) object;
            NewsBuilder newsBuilder = WxMpXmlOutMessage.NEWS();
            WxMpXmlOutNewsMessage.Item item = new WxMpXmlOutNewsMessage.Item();
            item.setDescription(wxMpNewsArticle.getDigest());
            item.setPicUrl(wxMpNewsArticle.getThumbUrl());
            item.setTitle(wxMpNewsArticle.getTitle());
            item.setUrl(wxMpNewsArticle.getUrl());
            newsBuilder.addArticle(item);
            builder = newsBuilder;
        } else {
            throw new RuntimeException("错误的对象类型");
        }

        return builder;
    }


    private BaseBuilder getWxMpXmlOutMessageBuilder(String keyword, List<SearchHit<SearchContentPO>> searchHits, Map<Long, String> weixinArticleIdToUrlMap) {
        BaseBuilder builder;
        if (CollectionUtils.isEmpty(searchHits)) {

            //  关键词 ： %s
            //  —— —— —— —— —— ——
            //
            //  没有找到相关资源
            String contentFormat = "关键词 ： %s\n" +
                    "—— —— —— —— —— ——\n\n" +
                    "没有找到相关资源";
            String resultMsg = String.format(contentFormat, keyword);
            TextBuilder textBuilder = WxMpXmlOutMessage.TEXT();
            textBuilder.content(resultMsg);
            builder = textBuilder;
        } else {
            //  关键词 ： %s
            //  —— —— —— —— —— ——
            //  匹配的资源(最多显示6条)
            //  —— —— —— —— —— ——
            //  1. %s
            //  2. %s
            String contentFormat = "关键词 ： %s\n" +
                    "—— —— —— —— —— ——\n" +
                    "相关度 : 资源名称(最多6条)\n" +
                    "—— —— —— —— —— ——\n";
            StringBuilder resultMsg = new StringBuilder(String.format(contentFormat, keyword));
            for (SearchHit<SearchContentPO> searchHit : searchHits) {
                String itemFormat = "%s : %s\n";
                String title;
                SearchContentPO searchContent = searchHit.getContent();
                title = searchContent.getTitle();
                String scoreStr = scoreDecimalFormat.format(searchHit.getScore());
                String itemContent;
                if (weixinArticleIdToUrlMap != null && weixinArticleIdToUrlMap.size() > 0) {
                    itemFormat = "%s : %s";
                    itemContent = String.format(itemFormat, scoreStr, title);
                    itemContent = String.format("<a href=\"%s\">%s</a>\n", weixinArticleIdToUrlMap.get(searchContent.getOriginalId()), itemContent);
                } else {
                    itemContent = String.format(itemFormat, scoreStr, title);
                }
                resultMsg.append(itemContent);
            }
            TextBuilder textBuilder = WxMpXmlOutMessage.TEXT();
            textBuilder.content(resultMsg.toString());
            builder = textBuilder;
        }
        return builder;
    }

    /**
     * 拉取永久素材中的图文
     */
    @Override
    public void materialNewsSynchronizer(String appTag, WxMpMaterialService wxMpMaterialService) throws WxErrorException {
        int startIndex = 0;
        int size = 20;
        WxMpMaterialCountResult wxMpMaterialCountResult = wxMpMaterialService.materialCount();
        log.info("appTag = {}, wxMpMaterialCountResult = {}", appTag, wxMpMaterialCountResult);
        while (startIndex <= wxMpMaterialCountResult.getNewsCount()) {
            WxMpMaterialNewsBatchGetResult wxMpMaterialNewsBatchGetResult = wxMpMaterialService.materialNewsBatchGet(startIndex, size);
            log.info("appTag = {}, startIndex = {}, size = {}, wxMpMaterialNewsBatchGetResult.itemCount = {}",
                    appTag, startIndex, size, wxMpMaterialNewsBatchGetResult.getItemCount());
            for (WxMpMaterialNewsBatchGetResult.WxMaterialNewsBatchGetNewsItem item : wxMpMaterialNewsBatchGetResult.getItems()) {
                for (WxMpNewsArticle article : item.getContent().getArticles()) {
                    WxMpNewsArticlePO wxMpNewsArticle = WxMpNewsArticlePO.createDefault(WxMpNewsArticlePO.class);
                    BeanUtils.copyProperties(article, wxMpNewsArticle);
                    wxMpNewsArticleService.insert(appTag, wxMpNewsArticle);
                }
            }
            startIndex += size;
        }
    }

    /**
     * 拉取已发布的文章
     */
    @Override
    public void freePublishSynchronizer(String appTag, WxMpFreePublishService freePublishService) throws WxErrorException {
        int startIndex = 0;
        int size = 10;
        Integer totalCount;
        Integer itemCount;
        do {
            WxMpFreePublishList wxMpFreePublishList = freePublishService.getPublicationRecords(startIndex, size);
            totalCount = wxMpFreePublishList.getTotalCount();
            itemCount = wxMpFreePublishList.getItemCount();
            log.info("appTag = {}, totalCount = {}, itemCount = {}", appTag, totalCount, itemCount);
            List<WxMpFreePublishItem> items = wxMpFreePublishList.getItems();
            if (CollectionUtils.isNotEmpty(items)) {
                for (WxMpFreePublishItem item : items) {
                    if (item != null && item.getContent() != null && item.getContent().getNewsItem() != null) {
                        List<WxMpFreePublishArticles> freePublishArticles = item.getContent().getNewsItem();
                        for (WxMpFreePublishArticles freePublishArticle : freePublishArticles) {
                            WxMpNewsArticlePO wxMpNewsArticle = WxMpNewsArticlePO.createDefault(WxMpNewsArticlePO.class);
                            wxMpNewsArticle.setUrl(freePublishArticle.getUrl());
                            wxMpNewsArticle.setThumbMediaId(freePublishArticle.getThumbMediaId());
                            wxMpNewsArticle.setAuthor(freePublishArticle.getAuthor());
                            wxMpNewsArticle.setTitle(freePublishArticle.getTitle());
                            wxMpNewsArticle.setContentSourceUrl(freePublishArticle.getContentSourceUrl());
                            wxMpNewsArticle.setContent(freePublishArticle.getContent());
                            wxMpNewsArticle.setDigest(freePublishArticle.getDigest());
                            Integer showCoverPic = freePublishArticle.getShowCoverPic();
                            wxMpNewsArticle.setShowCoverPic(showCoverPic != null && showCoverPic == 1);
                            wxMpNewsArticle.setUrl(freePublishArticle.getUrl());
                            Integer needOpenComment = freePublishArticle.getNeedOpenComment();
                            wxMpNewsArticle.setNeedOpenComment(needOpenComment != null && needOpenComment == 1);
                            Integer onlyFansCanComment = freePublishArticle.getOnlyFansCanComment();
                            wxMpNewsArticle.setOnlyFansCanComment(onlyFansCanComment != null && onlyFansCanComment == 1);
                            wxMpNewsArticle.setAppTag(appTag);
                            wxMpNewsArticleService.insert(appTag, wxMpNewsArticle);
                        }
                    }
                }
            }
        } while (startIndex > (totalCount + size));
    }

    @Override
    public void reindexEsContent(String appTag) {
        if (reindexEsContentLock.tryLock()) {
            try {
                int pageNum = 0;
                int pageSize = 10;
                Page<WxMpNewsArticlePO> page;
                do {
                    pageNum++;
                    page = wxMpNewsArticleService.page(pageNum, pageSize);
                    List<WxMpNewsArticlePO> wxMpNewsArticles = page.getContent();
                    if (CollectionUtils.isNotEmpty(wxMpNewsArticles)) {
                        for (WxMpNewsArticlePO wxMpNewsArticle : wxMpNewsArticles) {
                            ResourceUpdateEvent resourceUpdateEvent = new ResourceUpdateEvent(wxMpNewsArticle.getId(), ResourceType.WEIXIN_ARTICLE);
                            applicationContext.publishEvent(resourceUpdateEvent);
                        }
                    }
                } while (!page.isEmpty());
            } finally {
                reindexEsContentLock.unlock();
            }
        } else {
            throw new ServiceException("服务器忙不过来了,请稍后再试!");
        }
    }
}
