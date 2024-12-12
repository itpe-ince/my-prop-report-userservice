package com.dnc.mprs.userservice.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.dnc.mprs.userservice.domain.UserInfo;
import com.dnc.mprs.userservice.repository.UserInfoRepository;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;

/**
 * Spring Data Elasticsearch repository for the {@link UserInfo} entity.
 */
public interface UserInfoSearchRepository extends ElasticsearchRepository<UserInfo, Long>, UserInfoSearchRepositoryInternal {}

interface UserInfoSearchRepositoryInternal {
    Page<UserInfo> search(String query, Pageable pageable);

    Page<UserInfo> search(Query query);

    @Async
    void index(UserInfo entity);

    @Async
    void deleteFromIndexById(Long id);
}

class UserInfoSearchRepositoryInternalImpl implements UserInfoSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final UserInfoRepository repository;

    UserInfoSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, UserInfoRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<UserInfo> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<UserInfo> search(Query query) {
        SearchHits<UserInfo> searchHits = elasticsearchTemplate.search(query, UserInfo.class);
        List<UserInfo> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(UserInfo entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), UserInfo.class);
    }
}
