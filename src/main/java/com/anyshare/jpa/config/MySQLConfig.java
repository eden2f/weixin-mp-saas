package com.anyshare.jpa.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * @author huangminpeng
 * @date 2021/4/10 18:03
 */
@Configuration
@EnableJpaRepositories(basePackages = "com.anyshare.jpa.mysql")
public class MySQLConfig {
}
