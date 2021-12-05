package com.anyshare.service;

import com.anyshare.exception.ServiceException;
import com.anyshare.jpa.mysql.po.AppOpenApiConfigPO;
import com.anyshare.service.common.AppOpenApiConfigService;
import com.anyshare.web.config.WxMpConfig;
import com.anyshare.web.dto.weixin.OpenapiConfigAddReq;
import com.anyshare.web.dto.weixin.OpenapiConfigUpdateReq;
import com.anyshare.web.dto.weixin.OpenapiReindexSearchContentReq;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpMaterialService;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import me.chanjar.weixin.mp.config.WxMpConfigStorage;
import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl;
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

    private WxMpService materialNewsSynchronizer(String appTag, WxMpConfigStorage wxMpConfigStorage) {
        WxMpService wxMpService = new WxMpServiceImpl();
        wxMpService.setWxMpConfigStorage(wxMpConfigStorage);
        WxMpMaterialService wxMpMaterialService = wxMpService.getMaterialService();
        try {
            weixinService.materialNewsSynchronizer(appTag, wxMpMaterialService);
        } catch (WxErrorException we) {
            log.info(String.format("同步微信物料发生异常, %s", appTag), we);
            throw new ServiceException("请检查下微信公众配置有误,请稍后重试!");
        }
        return wxMpService;
    }

}
