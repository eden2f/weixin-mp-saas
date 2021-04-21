package com.anyshare.jpa.es.repository;

import com.anyshare.jpa.es.po.SearchContentPO;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

/**
 * @author huangminpeng
 * @date 2021/4/7 10:55
 */
public interface SearchContentRepository extends ElasticsearchRepository<SearchContentPO, Long> {

    List<SearchContentPO> findByTitle(String title);

}
