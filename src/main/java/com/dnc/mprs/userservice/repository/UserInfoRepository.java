package com.dnc.mprs.userservice.repository;

import com.dnc.mprs.userservice.domain.UserInfo;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the UserInfo entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UserInfoRepository extends ReactiveCrudRepository<UserInfo, Long>, UserInfoRepositoryInternal {
    Flux<UserInfo> findAllBy(Pageable pageable);

    @Override
    <S extends UserInfo> Mono<S> save(S entity);

    @Override
    Flux<UserInfo> findAll();

    @Override
    Mono<UserInfo> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface UserInfoRepositoryInternal {
    <S extends UserInfo> Mono<S> save(S entity);

    Flux<UserInfo> findAllBy(Pageable pageable);

    Flux<UserInfo> findAll();

    Mono<UserInfo> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<UserInfo> findAllBy(Pageable pageable, Criteria criteria);
}
