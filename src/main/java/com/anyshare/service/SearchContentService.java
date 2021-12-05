package com.anyshare.service;

import com.anyshare.jpa.es.po.SearchContentPO;
import org.springframework.data.elasticsearch.core.SearchHits;

import java.util.List;

/**
 * @author Eden
 * @date 2021/4/7 12:13
 */
public interface SearchContentService {

    void save(List<SearchContentPO> list);

    void save(SearchContentPO bean);

    void saveOrUpdate(SearchContentPO searchContent);

    List<SearchContentPO> findByTitle(String title);

    SearchHits<SearchContentPO> findByTitleOrDigestOrContent(String appTag, String searchKey);
}
