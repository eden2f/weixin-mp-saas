package com.anyshare.service;

import com.anyshare.enums.AppTag;
import com.anyshare.jpa.es.po.SearchContentPO;
import com.anyshare.jpa.es.repository.SearchContentRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author huangminpeng
 * @date 2021/4/7 12:13
 */
@Slf4j
@Service
public class SearchContentServiceImpl implements SearchContentService {

    @Resource
    private SearchContentRepository searchContentRepository;
    @Resource
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Override
    public void save(List<SearchContentPO> searchContents) {
        searchContentRepository.saveAll(searchContents);

    }

    @Override
    public void save(SearchContentPO searchContent) {
        searchContentRepository.save(searchContent);
    }

    @Override
    public List<SearchContentPO> findByTitle(String title) {
        return searchContentRepository.findByTitle(title);
    }

    @Override
    public List<SearchContentPO> findByTitleOrDigestOrContent(AppTag appTag, String searchKey) {
        Assert.isTrue(StringUtils.isNoneBlank(searchKey), "搜索关键字不能为空");
        Pageable pageable = PageRequest.of(0, 6);
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        TermQueryBuilder appTagQueryBuilder = new TermQueryBuilder("appTag", appTag.getCode());
        TermQueryBuilder titleTagQueryBuilder = new TermQueryBuilder("title", searchKey);
        TermQueryBuilder digestTagQueryBuilder = new TermQueryBuilder("digest", searchKey);
        TermQueryBuilder contentTagQueryBuilder = new TermQueryBuilder("content", searchKey);
        boolQueryBuilder.filter(appTagQueryBuilder)
                .should(titleTagQueryBuilder)
                .should(digestTagQueryBuilder)
                .should(contentTagQueryBuilder);
        boolQueryBuilder.minimumShouldMatch(1);
        boolQueryBuilder.boost(1);
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(boolQueryBuilder);
        nativeSearchQuery.setPageable(pageable);
        SearchHits<SearchContentPO> searchHits = elasticsearchRestTemplate.search(nativeSearchQuery, SearchContentPO.class);
        List<SearchHit<SearchContentPO>> searchHitList = searchHits.getSearchHits();
        return searchHitList.stream().map(SearchHit::getContent).collect(Collectors.toList());
    }
}
