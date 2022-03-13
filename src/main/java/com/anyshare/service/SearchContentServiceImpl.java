package com.anyshare.service;

import com.anyshare.jpa.es.po.SearchContentPO;
import com.anyshare.jpa.es.repository.SearchContentRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

/**
 * @author Eden
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
    public void saveOrUpdate(SearchContentPO searchContent) {
        if (searchContent.getId() != null) {
            Optional<SearchContentPO> optional = searchContentRepository.findById(searchContent.getId());
            if (optional.isPresent()) {
                searchContentRepository.save(searchContent);
                return;
            }
        }
        List<SearchContentPO> searchContents = searchContentRepository.findByAppTagAndResourceTypeAndOriginalId(searchContent.getAppTag(), searchContent.getResourceType(), searchContent.getOriginalId());
        if (CollectionUtils.isNotEmpty(searchContents)) {
            for (int i = 0; i < searchContents.size(); i++) {
                SearchContentPO content = searchContents.get(i);
                if (i == 0) {
                    searchContent.setId(content.getId());
                    searchContentRepository.save(searchContent);
                } else {
                    searchContentRepository.delete(content);
                }
            }
        } else {
            searchContentRepository.save(searchContent);
        }
    }

    @Override
    public List<SearchContentPO> findByTitle(String title) {
        return searchContentRepository.findByTitle(title);
    }

    @Override
    public SearchHits<SearchContentPO> findByTitleOrDigestOrContent(String appTag, String searchKey) {
        Assert.isTrue(StringUtils.isNoneBlank(searchKey), "搜索关键字不能为空");
        Pageable pageable = PageRequest.of(0, 6);
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        TermQueryBuilder appTagQueryBuilder = new TermQueryBuilder("appTag", appTag);
        MatchQueryBuilder titleTagQueryBuilder = new MatchQueryBuilder("title", searchKey);
        MatchQueryBuilder digestTagQueryBuilder = new MatchQueryBuilder("digest", searchKey);
        MatchQueryBuilder contentTagQueryBuilder = new MatchQueryBuilder("content", searchKey);
        boolQueryBuilder.filter(appTagQueryBuilder)
                .should(titleTagQueryBuilder)
                .should(digestTagQueryBuilder)
                .should(contentTagQueryBuilder);
        boolQueryBuilder.minimumShouldMatch(1);
        boolQueryBuilder.boost(1);
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(boolQueryBuilder);
        nativeSearchQuery.setPageable(pageable);
        return elasticsearchRestTemplate.search(nativeSearchQuery, SearchContentPO.class);
    }

    @Override
    public SearchHits<SearchContentPO> findByAppTagsOrTitleOrDigestOrContent(List<String> appTags, String searchKey) {
        Assert.isTrue(StringUtils.isNoneBlank(searchKey), "搜索关键字不能为空");
        Pageable pageable = PageRequest.of(0, 6);
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        TermsQueryBuilder appTagQueryBuilder = new TermsQueryBuilder("appTag", appTags);
        boolQueryBuilder.filter(appTagQueryBuilder);
        MatchQueryBuilder titleTagQueryBuilder = new MatchQueryBuilder("title", searchKey);
        MatchQueryBuilder digestTagQueryBuilder = new MatchQueryBuilder("digest", searchKey);
        MatchQueryBuilder contentTagQueryBuilder = new MatchQueryBuilder("content", searchKey);
        boolQueryBuilder.should(titleTagQueryBuilder)
                .should(digestTagQueryBuilder)
                .should(contentTagQueryBuilder);
        boolQueryBuilder.minimumShouldMatch(1);
        boolQueryBuilder.boost(1);
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(boolQueryBuilder);
        nativeSearchQuery.setPageable(pageable);
        return elasticsearchRestTemplate.search(nativeSearchQuery, SearchContentPO.class);
    }
}
