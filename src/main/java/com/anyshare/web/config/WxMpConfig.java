package com.anyshare.web.config;

import com.anyshare.jpa.mysql.po.AppOpenApiConfigPO;
import com.anyshare.service.common.AppOpenApiConfigService;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import me.chanjar.weixin.mp.config.WxMpConfigStorage;
import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * todo 新增/修改配置时需要通知修改map
 */
@Configuration
public class WxMpConfig {

    private static Map<String, AppOpenApiConfigPO> APP_OPEN_API_CONFIG_MAP;
    private static final Map<String, WxMpConfigStorage> WX_MP_CONFIG_STORAGE_MAP = new HashMap<>();
    public static final Map<String, WxMpService> WX_MP_SERVICE_MAP = new HashMap<>();

    public static void addWxMpConfig(String appTag, AppOpenApiConfigPO appOpenApiConfig, WxMpService wxMpService, WxMpConfigStorage wxMpConfigStorage) {
        APP_OPEN_API_CONFIG_MAP.put(appTag, appOpenApiConfig);
        WX_MP_CONFIG_STORAGE_MAP.put(appTag, wxMpConfigStorage);
        WX_MP_SERVICE_MAP.put(appTag, wxMpService);
    }

    public static String getWxMpVerifyKey(String appTag, String verifyKey) {
        if (APP_OPEN_API_CONFIG_MAP.containsKey(appTag)) {
            AppOpenApiConfigPO appOpenApiConfig = APP_OPEN_API_CONFIG_MAP.get(appTag);
            if (appOpenApiConfig.getVerifyKey().equals(verifyKey)) {
                return appOpenApiConfig.getVerifyValue();
            }
        }
        return System.currentTimeMillis() + "";
    }

    public static WxMpConfigStorage getWxMpConfigStorageByAppTag(@NotNull String appTag) {
        return WX_MP_CONFIG_STORAGE_MAP.get(appTag);
    }

    public static WxMpService getWxMpServiceByAppTag(@NotNull String appTag) {
        return WX_MP_SERVICE_MAP.get(appTag);
    }


    @Bean("appOpenApiConfigMap")
    public Map<String, AppOpenApiConfigPO> appOpenApiConfigMap(AppOpenApiConfigService appOpenApiConfigService) {
        List<AppOpenApiConfigPO> appOpenApiConfigs = appOpenApiConfigService.findAll();
        APP_OPEN_API_CONFIG_MAP = appOpenApiConfigs.stream().collect(Collectors.toMap(AppOpenApiConfigPO::getAppTag, item -> item));
        return APP_OPEN_API_CONFIG_MAP;
    }


    @Bean("wxMpConfigStorageMap")
    public Map<String, WxMpConfigStorage> wxMpConfigStorageMap(Map<String, AppOpenApiConfigPO> appOpenApiConfigMap) {
        for (AppOpenApiConfigPO appOpenApiConfig : appOpenApiConfigMap.values()) {
            WxMpDefaultConfigImpl config = new WxMpDefaultConfigImpl();
            config.setAppId(appOpenApiConfig.getAppId());
            config.setSecret(appOpenApiConfig.getSecret());
            config.setToken(appOpenApiConfig.getToken());
            config.setAesKey(appOpenApiConfig.getAesKey());
            WX_MP_CONFIG_STORAGE_MAP.put(appOpenApiConfig.getAppTag(), config);
        }
        return WX_MP_CONFIG_STORAGE_MAP;
    }


    @Bean("wxMpServiceMap")
    public Map<String, WxMpService> wxMpServiceMap(Map<String, WxMpConfigStorage> wxMpConfigStorageMap) {
        for (Map.Entry<String, WxMpConfigStorage> wxMpConfigStorageEntry : wxMpConfigStorageMap.entrySet()) {
            WxMpService wxService = new WxMpServiceImpl();
            wxService.setWxMpConfigStorage(wxMpConfigStorageEntry.getValue());
            WX_MP_SERVICE_MAP.put(wxMpConfigStorageEntry.getKey(), wxService);
        }
        return WX_MP_SERVICE_MAP;
    }
}
