package com.dnc.mprs.userservice.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.dnc.mprs.userservice.domain.UserInfo;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.ReactiveElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import reactor.core.publisher.Flux;

/**
 * Spring Data Elasticsearch repository for the {@link UserInfo} entity.
 */
public interface UserInfoSearchRepository extends ReactiveElasticsearchRepository<UserInfo, Long>, UserInfoSearchRepositoryInternal {}

interface UserInfoSearchRepositoryInternal {
    Flux<UserInfo> search(String query, Pageable pageable);

    Flux<UserInfo> search(Query query);
}

class UserInfoSearchRepositoryInternalImpl implements UserInfoSearchRepositoryInternal {

    private final ReactiveElasticsearchTemplate reactiveElasticsearchTemplate;

    UserInfoSearchRepositoryInternalImpl(ReactiveElasticsearchTemplate reactiveElasticsearchTemplate) {
        this.reactiveElasticsearchTemplate = reactiveElasticsearchTemplate;
    }

    @Override
    public Flux<UserInfo> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        nativeQuery.setPageable(pageable);
        return search(nativeQuery);
    }

    @Override
    public Flux<UserInfo> search(Query query) {
        return reactiveElasticsearchTemplate.search(query, UserInfo.class).map(SearchHit::getContent);
    }
}
