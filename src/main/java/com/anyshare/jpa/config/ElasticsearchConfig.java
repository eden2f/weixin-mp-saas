package com.anyshare.jpa.config;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

import java.time.Duration;

/**
 * @author Eden
 * @date 2021/4/10 18:50
 */
@Configuration
@EnableElasticsearchRepositories(basePackages = "com.anyshare.jpa.es", includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = ElasticsearchRepository.class))
public class ElasticsearchConfig {

    @Value("${elasticsearch.hostAndPort}")
    private String hostAndPort;
    @Value("${elasticsearch.username}")
    private String username;
    @Value("${elasticsearch.password}")
    private String password;

    @Bean
    public RestHighLevelClient elasticsearchClient() {
        ClientConfiguration.TerminalClientConfigurationBuilder builder = ClientConfiguration.builder()
                .connectedTo(hostAndPort)
                .withConnectTimeout(Duration.ofSeconds(5))
                .withSocketTimeout(Duration.ofSeconds(3));
        //.useSsl()
        //.withDefaultHeaders(defaultHeaders)
        if (StringUtils.isNotBlank(username) && StringUtils.isNotBlank(password)) {
            builder.withBasicAuth(username, password);
        }
        // ... other options
        ClientConfiguration configuration = builder.build();
        return RestClients.create(configuration).rest();
    }
}
