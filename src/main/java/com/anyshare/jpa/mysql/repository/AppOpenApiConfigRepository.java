package com.anyshare.jpa.mysql.repository;

import com.anyshare.jpa.mysql.po.AppOpenApiConfigPO;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Eden
 * @date 2020/07/25
 */
public interface AppOpenApiConfigRepository extends JpaRepository<AppOpenApiConfigPO, Long> {

    AppOpenApiConfigPO findByAppTag(String appTag);

    AppOpenApiConfigPO findByAppid(String appid);

}