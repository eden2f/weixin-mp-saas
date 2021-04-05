package com.anyshare.service;

import cn.hutool.core.util.RandomUtil;
import com.anyshare.enums.AppTag;
import com.anyshare.jpa.po.ShareResourcePO;
import com.anyshare.service.common.ShareResourceService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Eden
 * @date 2020/07/25
 */
@SpringBootTest
public class ShareResourceServiceTest {

    @Resource
    private ShareResourceService shareResourceService;

    @Test
    void insert() {
        ShareResourcePO shareResource = ShareResourcePO.createDefault(ShareResourcePO.class);
        shareResource.setName("Test ShareResource" + RandomUtil.randomInt());
        shareResource.setContent("ShareResource ShareResource ShareResource ShareResource ShareResource");
        shareResource.setWeixinPushContent("ShareResource ShareResource ShareResource ShareResource ShareResource");
        Long id = shareResourceService.insert(AppTag.Test.getCode(), shareResource);
        Assert.isTrue(id != null, "insert");
    }

    @Test
    void selectByNameLikeTop3() {
        insert();
        List<ShareResourcePO> testShareResources = shareResourceService.findTop6ByNameContaining(AppTag.Test.getCode(), "ShareResource");
        Assert.isTrue(!CollectionUtils.isEmpty(testShareResources), "selectByNameLikeTop3");
        List<ShareResourcePO> dailyShareResources = shareResourceService.findTop6ByNameContaining(AppTag.Daily.getCode(), "ShareResource");
        Assert.isTrue(CollectionUtils.isEmpty(dailyShareResources), "selectByNameLikeTop3");
    }
}