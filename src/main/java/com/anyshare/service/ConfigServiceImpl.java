package com.anyshare.service;

import com.anyshare.exception.ServiceException;
import com.anyshare.jpa.po.AppOpenApiConfigPO;
import com.anyshare.service.common.AppOpenApiConfigService;
import com.anyshare.web.config.WxMpConfig;
import com.anyshare.web.dto.weixin.OpenapiConfigAddReq;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpMaterialService;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
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
    public void openapiConfigAdd(OpenapiConfigAddReq req) throws WxErrorException {
        AppOpenApiConfigPO appOpenApiConfig = appOpenApiConfigService.findByAppTag(req.getAppTag());
        if (appOpenApiConfig != null) {
            return;
        }
        // 1. 拉取历史文章(同时可以验证配置是否正确)
        WxMpDefaultConfigImpl wxMpConfig = new WxMpDefaultConfigImpl();
        wxMpConfig.setAppId(req.getAppid());
        wxMpConfig.setSecret(req.getSecret());
        wxMpConfig.setToken(req.getToken());
        wxMpConfig.setAesKey(req.getAesKey());
        WxMpService wxMpService = new WxMpServiceImpl();
        wxMpService.setWxMpConfigStorage(wxMpConfig);
        WxMpMaterialService wxMpMaterialService = wxMpService.getMaterialService();
        try{
            weixinService.materialNewsSynchronism(req.getAppTag(), wxMpMaterialService);
        } catch (WxErrorException we){
            throw new ServiceException("请检查下微信公众配置有误,请稍后重试!");
        }
        // 2. 保存到数据库
        appOpenApiConfig = AppOpenApiConfigPO.createDefault(AppOpenApiConfigPO.class);
        BeanUtils.copyProperties(req, appOpenApiConfig);
        appOpenApiConfigService.insert(appOpenApiConfig);
        // 3. 更新内存存储配置
        WxMpConfig.addWxMpConfig(req.getAppTag(), appOpenApiConfig, wxMpService, wxMpConfig);
    }


}
