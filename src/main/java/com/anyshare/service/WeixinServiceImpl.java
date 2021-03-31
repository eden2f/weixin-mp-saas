package com.anyshare.service;

import cn.hutool.core.util.RandomUtil;
import com.anyshare.jpa.po.BasePO;
import com.anyshare.jpa.po.ShareResourcePO;
import com.anyshare.jpa.po.WxMpNewsArticlePO;
import com.anyshare.service.common.ShareResourceService;
import com.anyshare.service.common.WxMpNewsArticleService;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpMaterialService;
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
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

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

    @Override
    public WxMpXmlOutMessage handle(String appTag, WxMpXmlMessage inMessage) {
        log.info("appTag = {}, inMessage = {}", appTag, inMessage);
        String keyword = inMessage.getContent().trim();
        List<BasePO> pos = searchByKeyword(appTag, keyword);
        BaseBuilder wxMpXmlOutMessageBuilder = getWxMpXmlOutMessageBuilder(keyword, pos);
        wxMpXmlOutMessageBuilder.fromUser(inMessage.getToUser());
        wxMpXmlOutMessageBuilder.toUser(inMessage.getFromUser());
        WxMpXmlOutMessage outMessage = (WxMpXmlOutMessage) wxMpXmlOutMessageBuilder.build();
        log.info("outMessage = {}", outMessage);
        return outMessage;
    }

    private List<BasePO> searchByKeyword(String appTag, String keyword) {
        List<ShareResourcePO> shareResources = shareResourceService.findTop6ByNameContaining(appTag, keyword);
        List<BasePO> pos = new ArrayList<>(shareResources);
        List<WxMpNewsArticlePO> wxMpNewsArticles = wxMpNewsArticleService.findTop6ByTitleContaining(appTag, keyword);
        pos.addAll(wxMpNewsArticles);
        return RandomUtil.randomEleList(pos, 6);
    }

    private BaseBuilder getWxMpXmlOutMessageBuilder(String keyword, List<BasePO> objects) {
        BaseBuilder builder;
        if (CollectionUtils.isEmpty(objects)) {

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
            if (CollectionUtils.size(objects) > 1) {
                //  关键词 ： %s
                //  —— —— —— —— —— ——
                //  匹配的资源(最多显示6条)
                //  —— —— —— —— —— ——
                //  1. %s
                //  2. %s
                String contentFormat = "关键词 ： %s\n" +
                        "—— —— —— —— —— ——\n" +
                        "匹配的资源(最多显示6条)\n" +
                        "—— —— —— —— —— ——\n";
                StringBuilder resultMsg = new StringBuilder(String.format(contentFormat, keyword));
                for (int i = 0; i < objects.size(); i++) {
                    Object object = objects.get(i);
                    String itemFormat = "%s. %s\n";
                    String title;
                    if (object instanceof ShareResourcePO) {
                        ShareResourcePO shareResource = (ShareResourcePO) object;
                        title = shareResource.getName();
                    } else if (object instanceof WxMpNewsArticlePO) {
                        WxMpNewsArticlePO wxMpNewsArticle = (WxMpNewsArticlePO) object;
                        title = wxMpNewsArticle.getTitle();
                    } else {
                        throw new RuntimeException("错误的对象类型");
                    }
                    String itemContent = String.format(itemFormat, i + 1, title);
                    resultMsg.append(itemContent);
                }
                TextBuilder textBuilder = WxMpXmlOutMessage.TEXT();
                textBuilder.content(resultMsg.toString());
                builder = textBuilder;
            } else {
                Object object = objects.get(0);
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
            }

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
}
