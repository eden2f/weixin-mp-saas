package com.anyshare.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.anyshare.enums.DelStatus;
import com.anyshare.exception.ServiceException;
import com.anyshare.jpa.mysql.po.AppOpenApiConfigPO;
import com.anyshare.service.common.AppOpenApiConfigService;
import com.anyshare.web.config.WxMpConfig;
import com.anyshare.web.dto.weixin.OpenapiConfigAddReq;
import com.anyshare.web.dto.weixin.OpenapiConfigDrainageEnableReq;
import com.anyshare.web.dto.weixin.OpenapiConfigUpdateReq;
import com.anyshare.web.dto.weixin.OpenapiReindexSearchContentReq;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpMaterialService;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import me.chanjar.weixin.mp.bean.guide.WxMpAddGuideAutoReply;
import me.chanjar.weixin.mp.config.WxMpConfigStorage;
import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author Eden
 * @date 2020/07/25
 */
@Slf4j
@Service
public class ConfigServiceImpl implements ConfigService {

    @Resource
    private AppOpenApiConfigService appOpenApiConfigService;
    @Resource
    private WeixinService weixinService;

    /**
     * 1. 拉取历史文章(同时可以验证配置是否正确)
     * 2. 保存到数据库
     * 3. 更新内存存储配置
     */
    @Override
    public void openapiConfigAdd(OpenapiConfigAddReq req) {
        AppOpenApiConfigPO appOpenApiConfig = appOpenApiConfigService.findByAppTag(req.getAppTag());
        if (appOpenApiConfig != null) {
            throw new ServiceException("appTag已被占用，换一个试试？");
        }
        // 1. 拉取历史文章(同时可以验证配置是否正确)
        WxMpDefaultConfigImpl wxMpConfig = new WxMpDefaultConfigImpl();
        BeanUtils.copyProperties(req, wxMpConfig);
        WxMpService wxMpService = materialNewsSynchronizer(req.getAppTag(), wxMpConfig);
        // 2. 保存到数据库
        appOpenApiConfig = AppOpenApiConfigPO.createDefault(AppOpenApiConfigPO.class);
        BeanUtils.copyProperties(req, appOpenApiConfig);
        appOpenApiConfigService.saveOrUpdate(appOpenApiConfig);
        // 3. 更新内存存储配置
        WxMpConfig.addWxMpConfig(req.getAppTag(), appOpenApiConfig, wxMpService, wxMpConfig);
    }

    /**
     * 1. 拉取历史文章(同时可以验证配置是否正确)
     * 2. 保存到数据库
     * 3. 更新内存存储配置
     */
    @Override
    public void openapiConfigUpdate(OpenapiConfigUpdateReq req) {
        AppOpenApiConfigPO appOpenApiConfig = checkSecretGetApiConfig(req.getAppTag(), req.getOpenapiConfigVerify().getSecret());
        // 1. 拉取历史文章(同时可以验证配置是否正确)
        WxMpDefaultConfigImpl wxMpConfig = new WxMpDefaultConfigImpl();
        BeanUtils.copyProperties(req, wxMpConfig);
        WxMpService wxMpService = materialNewsSynchronizer(req.getAppTag(), wxMpConfig);
        // 2. 保存到数据库
        BeanUtils.copyProperties(req, appOpenApiConfig);
        appOpenApiConfigService.saveOrUpdate(appOpenApiConfig);
        // 3. 更新内存存储配置
        WxMpConfig.addWxMpConfig(req.getAppTag(), appOpenApiConfig, wxMpService, wxMpConfig);
    }

    private AppOpenApiConfigPO checkSecretGetApiConfig(String appTag, String secret) {
        AppOpenApiConfigPO appOpenApiConfig = appOpenApiConfigService.findByAppTag(appTag);
        if (appOpenApiConfig == null) {
            throw new ServiceException("appTag不存在");
        }
        // 0. 验证
        if (!appOpenApiConfig.getSecret().equals(secret)) {
            throw new ServiceException("openapiConfigVerify.secret不正确!");
        }
        return appOpenApiConfig;
    }

    @Override
    public void reindexSearchContent(OpenapiReindexSearchContentReq req) {
        checkSecretGetApiConfig(req.getAppTag(), req.getSecret());
        weixinService.reindexEsContent(req.getAppTag());
    }

    @Override
    public void drainageEnable(OpenapiConfigDrainageEnableReq req) {
        AppOpenApiConfigPO appOpenApiConfigPo = checkSecretGetApiConfig(req.getAppTag(), req.getSecret());
        appOpenApiConfigPo.setDrainageEnable(BooleanUtils.isTrue(req.isEnable()) ? DelStatus.DELETED.getCode() : DelStatus.VALID.getCode());
        appOpenApiConfigService.saveOrUpdate(appOpenApiConfigPo);
    }

    @Override
    public void drainageCloseForAdmin(String appTag) {
        AppOpenApiConfigPO appOpenApiConfigPo = appOpenApiConfigService.findByAppTag(appTag);
        appOpenApiConfigPo.setDrainageEnable(DelStatus.VALID.getCode());
        appOpenApiConfigService.saveOrUpdate(appOpenApiConfigPo);
    }

    private WxMpService materialNewsSynchronizer(String appTag, WxMpConfigStorage wxMpConfigStorage) {
        boolean fail = false;
        WxMpService wxMpService = new WxMpServiceImpl();
        wxMpService.setWxMpConfigStorage(wxMpConfigStorage);
        wxMpService.getDraftService();
        wxMpService.getFreePublishService();
        WxMpMaterialService wxMpMaterialService = wxMpService.getMaterialService();
        try {
            weixinService.materialNewsSynchronizer(appTag, wxMpMaterialService);
        } catch (WxErrorException we) {
            log.info(String.format("同步微信物料发生异常, %s", appTag), we);
            fail = true;
        }
        try {
            weixinService.freePublishSynchronizer(appTag, wxMpService.getFreePublishService());
        } catch (WxErrorException we) {
            log.info(String.format("同步微信物料发生异常, %s", appTag), we);
            fail = true;
        }
        if (fail) {
            throw new ServiceException("请检查下微信公众配置有误,请稍后重试!");
        }
        return wxMpService;
    }


    /**
     * 设置快捷回复与关注自动回复
     * 微信开发者文档 : https://developers.weixin.qq.com/doc/offiaccount/Shopping_Guide/guide-account/shopping-guide.setGuideConfig.html
     *
     * @param appTag 应用标识
     * @param secret 密钥
     * @param json   请求体
     * @throws WxErrorException 微信调用异常
     */
    @Override
    public void setGuideConfig(String appTag, String secret, String json) throws WxErrorException {
        checkSecretGetApiConfig(appTag, secret);
        WxMpService wxMpService = WxMpConfig.getWxMpServiceByAppTag(appTag);
        JSONObject jsonObject = JSON.parseObject(json);
        wxMpService.getGuideService().setGuideConfig(
                jsonObject.getString("guide_account"),
                jsonObject.getString("guide_openid"),
                jsonObject.getBoolean("is_delete"),
                jsonObject.getJSONArray("guide_fast_reply_list").toJavaList(String.class),
                WxMpAddGuideAutoReply.fromJson(jsonObject.getJSONObject("guide_auto_reply").toJSONString()),
                WxMpAddGuideAutoReply.fromJson(jsonObject.getJSONObject("guide_auto_reply_plus").toJSONString())
        );
    }

}
