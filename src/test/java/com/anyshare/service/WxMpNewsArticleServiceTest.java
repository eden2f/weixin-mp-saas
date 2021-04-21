package com.anyshare.service;

import com.anyshare.enums.AppTag;
import com.anyshare.jpa.mysql.po.WxMpNewsArticlePO;
import com.anyshare.service.common.WxMpNewsArticleService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

/**
 * @author Eden
 * @date 2021/2/3 13:57
 */
@SpringBootTest
public class WxMpNewsArticleServiceTest {


    @Resource
    private WxMpNewsArticleService wxMpNewsArticleService;

    @Test
    void insert() {
        WxMpNewsArticlePO wxMpNewsArticle = WxMpNewsArticlePO.createDefault(WxMpNewsArticlePO.class);
        wxMpNewsArticle.setThumbMediaId("http://www.baidu.com");
        wxMpNewsArticle.setThumbUrl("http://www.baidu.com");
        wxMpNewsArticle.setAuthor(System.currentTimeMillis() + "");
        wxMpNewsArticle.setTitle("http://www.baidu.com" + System.currentTimeMillis());
        wxMpNewsArticle.setContentSourceUrl("http://www.baidu.com");
        wxMpNewsArticle.setContent("http://www.baidu.com" + System.currentTimeMillis());
        wxMpNewsArticle.setDigest("http://www.baidu.com" + System.currentTimeMillis());
        wxMpNewsArticle.setShowCoverPic(false);
        wxMpNewsArticle.setUrl("http://www.baidu.com");
        wxMpNewsArticle.setNeedOpenComment(false);
        wxMpNewsArticle.setOnlyFansCanComment(false);
        wxMpNewsArticle.setAppTag(AppTag.Test.getCode());
        Long id = wxMpNewsArticleService.insert(AppTag.Test.getCode(), wxMpNewsArticle);
        Assert.isTrue(id != null, "insert");
    }

    @Test
    void findTop6ByTitleContaining() {
        insert();
        List<WxMpNewsArticlePO> testWxMpNewsArticles = wxMpNewsArticleService.findTop6ByTitleContaining(AppTag.Test.getCode(), "http://www.baidu.com");
        Assert.isTrue(!CollectionUtils.isEmpty(testWxMpNewsArticles), "findTop6ByTitleContaining");
        List<WxMpNewsArticlePO> dailyWxMpNewsArticles = wxMpNewsArticleService.findTop6ByTitleContaining(AppTag.Daily.getCode(), "http://www.baidu.com");
        Assert.isTrue(CollectionUtils.isEmpty(dailyWxMpNewsArticles), "findTop6ByTitleContaining");
    }

    @Test
    void findTopByTitle() {
        insert();
        List<WxMpNewsArticlePO> testWxMpNewsArticles = wxMpNewsArticleService.findTop6ByTitleContaining(AppTag.Test.getCode(), "http://www.baidu.com");
        Optional<WxMpNewsArticlePO> testWxMpNewsArticleOptional = wxMpNewsArticleService.findTopByTitle(AppTag.Test.getCode(), testWxMpNewsArticles.get(0).getTitle());
        Assert.isTrue(testWxMpNewsArticleOptional.isPresent(), "findTopByTitle");
        Optional<WxMpNewsArticlePO> dailyWxMpNewsArticleOptional = wxMpNewsArticleService.findTopByTitle(AppTag.Daily.getCode(), testWxMpNewsArticles.get(0).getTitle());
        Assert.isTrue(!dailyWxMpNewsArticleOptional.isPresent(), "findTopByTitle");
    }
}