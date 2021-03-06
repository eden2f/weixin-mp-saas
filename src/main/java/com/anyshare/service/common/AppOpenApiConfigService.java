package com.anyshare.service.common;

import com.anyshare.jpa.mysql.po.AppOpenApiConfigPO;

import java.util.List;

/**
 * @author Eden
 * @date 2021/2/3 14:47
 */
public interface AppOpenApiConfigService {

    AppOpenApiConfigPO findByAppTag(String appTag);

    Long saveOrUpdate(AppOpenApiConfigPO appOpenApiConfig);

    List<AppOpenApiConfigPO> findAll();
}
