package com.anyshare.jpa.es.po;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;

import static com.anyshare.jpa.es.po.SearchContentPO.ES_INDEX_PREFIX;
import static com.anyshare.jpa.es.po.SearchContentPO.ES_SEARCH_CONTENT;

/**
 * @author huangminpeng
 * @date 2021/4/7 10:51
 */
@Data
@Document(indexName = ES_INDEX_PREFIX + ES_SEARCH_CONTENT)
public class SearchContentPO implements Serializable {

    public static final String ES_INDEX_PREFIX = "weixin_mp_saas_";
    public static final String ES_SEARCH_CONTENT = "search_content";

    @Id
    private Long id;

    @Field(type = FieldType.Text, store = true)
    private String appTag;

    @Field(store = true, searchAnalyzer = "ik_smart", analyzer = "ik_max_word", type = FieldType.Text)
    private String title;

    @Field(store = true, searchAnalyzer = "ik_smart", analyzer = "ik_max_word", type = FieldType.Text)
    private String digest;

    @Field(store = true, searchAnalyzer = "ik_smart", analyzer = "ik_max_word", type = FieldType.Text)
    private String content;

    public static SearchContentPO create() {
        SearchContentPO searchContent = new SearchContentPO();
        searchContent.setId(0L);
        searchContent.setTitle("");
        searchContent.setDigest("");
        searchContent.setContent("");
        return searchContent;
    }
}
