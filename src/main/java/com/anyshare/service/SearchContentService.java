package com.anyshare.service;

import com.anyshare.enums.AppTag;
import com.anyshare.jpa.es.po.SearchContentPO;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * @author huangminpeng
 * @date 2021/4/7 12:13
 */
public interface SearchContentService {

    void save(List<SearchContentPO> list);

    void save(SearchContentPO bean);

    List<SearchContentPO> findByTitle(String title);

    List<SearchContentPO> findByTitleOrDigestOrContent(AppTag appTag, String text);

}
