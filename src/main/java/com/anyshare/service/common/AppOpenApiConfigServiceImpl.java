package com.anyshare.service.common;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.anyshare.jpa.po.AppOpenApiConfigPO;
import com.anyshare.jpa.repository.AppOpenApiConfigRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Eden
 * @date 2021/2/3 14:48
 */
@Slf4j
@Service
public class AppOpenApiConfigServiceImpl implements AppOpenApiConfigService {

    @Resource
    private AppOpenApiConfigRepository appOpenApiConfigRepository;

    private final Map<String, AppOpenApiConfigPO> appTagToConfigMap = new HashMap<>();

    private final AppOpenApiConfigPO emptyAppOpenApiConfig = new AppOpenApiConfigPO();

    @Override
    public AppOpenApiConfigPO findByAppTag(String appTag) {
        AppOpenApiConfigPO appOpenApiConfig = appTagToConfigMap.get(appTag);
        if (null == appOpenApiConfig) {
            appOpenApiConfig = appOpenApiConfigRepository.findByAppTag(appTag);
            if (appOpenApiConfig == null) {
                appTagToConfigMap.put(appTag, emptyAppOpenApiConfig);
                return null;
            }
        }
        return appOpenApiConfig == emptyAppOpenApiConfig ? null : appOpenApiConfig;
    }

    @Override
    public Long saveOrUpdate(AppOpenApiConfigPO appOpenApiConfig) {
        AppOpenApiConfigPO appOpenApiConfigInDb = findByAppTag(appOpenApiConfig.getAppTag());
        if (appOpenApiConfigInDb == null) {
            appOpenApiConfigInDb = appOpenApiConfigRepository.findByAppid(appOpenApiConfig.getAppid());
        }
        if (appOpenApiConfigInDb != null) {
            BeanUtil.copyProperties(appOpenApiConfig, appOpenApiConfigInDb, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));
            appOpenApiConfig = appOpenApiConfigInDb;
        }
        appOpenApiConfigRepository.save(appOpenApiConfig);
        appTagToConfigMap.put(appOpenApiConfig.getAppTag(), appOpenApiConfig);
        return appOpenApiConfig.getId();
    }

    @Override
    public List<AppOpenApiConfigPO> findAll() {
        return appOpenApiConfigRepository.findAll();
    }
}
