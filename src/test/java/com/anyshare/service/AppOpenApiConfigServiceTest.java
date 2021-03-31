package com.anyshare.service;

import cn.hutool.core.util.RandomUtil;
import com.anyshare.jpa.po.AppOpenApiConfigPO;
import com.anyshare.service.common.AppOpenApiConfigService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import javax.annotation.Resource;

/**
 * @author Eden
 * @date 2021/2/3 15:34
 */
@SpringBootTest
public class AppOpenApiConfigServiceTest {

    @Resource
    private AppOpenApiConfigService appOpenApiConfigService;

    private AppOpenApiConfigPO insertOne() {
        AppOpenApiConfigPO appOpenApiConfig = AppOpenApiConfigPO.createDefault(AppOpenApiConfigPO.class);
        appOpenApiConfig.setAppTag(RandomUtil.randomString(10));
        appOpenApiConfig.setAppid(RandomUtil.randomString(10));
        appOpenApiConfig.setSecret(RandomUtil.randomString(10));
        appOpenApiConfig.setToken(RandomUtil.randomString(10));
        appOpenApiConfig.setAesKey(RandomUtil.randomString(10));
        appOpenApiConfig.setVerifyKey(RandomUtil.randomString(10));
        appOpenApiConfig.setVerifyValue(RandomUtil.randomString(10));
        Long id = appOpenApiConfigService.insert(appOpenApiConfig);
        Assert.isTrue(id != null, "insert");
        return appOpenApiConfig;
    }

    @Test
    void insert() {
        AppOpenApiConfigPO appOpenApiConfig = insertOne();
        Assert.isTrue(appOpenApiConfig.getId() != null, "insert");
    }

    @Test
    void findByAppTag() {
        AppOpenApiConfigPO appOpenApiConfig = insertOne();
        AppOpenApiConfigPO appOpenApiConfig1 = appOpenApiConfigService.findByAppTag(appOpenApiConfig.getAppTag());
        Assert.isTrue(appOpenApiConfig1.getId().equals(appOpenApiConfig.getId()), "findByAppTag");
        AppOpenApiConfigPO appOpenApiConfig2 = appOpenApiConfigService.findByAppTag(RandomUtil.randomString(15));
        Assert.isTrue(appOpenApiConfig2 == null, "findByAppTag");
    }
}