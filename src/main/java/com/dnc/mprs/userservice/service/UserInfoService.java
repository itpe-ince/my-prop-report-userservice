package com.dnc.mprs.userservice.service;

import com.dnc.mprs.userservice.domain.UserInfo;
import com.dnc.mprs.userservice.repository.UserInfoRepository;
import com.dnc.mprs.userservice.repository.search.UserInfoSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.dnc.mprs.userservice.domain.UserInfo}.
 */
@Service
@Transactional
public class UserInfoService {

    private static final Logger LOG = LoggerFactory.getLogger(UserInfoService.class);

    private final UserInfoRepository userInfoRepository;

    private final UserInfoSearchRepository userInfoSearchRepository;

    public UserInfoService(UserInfoRepository userInfoRepository, UserInfoSearchRepository userInfoSearchRepository) {
        this.userInfoRepository = userInfoRepository;
        this.userInfoSearchRepository = userInfoSearchRepository;
    }

    /**
     * Save a userInfo.
     *
     * @param userInfo the entity to save.
     * @return the persisted entity.
     */
    public Mono<UserInfo> save(UserInfo userInfo) {
        LOG.debug("Request to save UserInfo : {}", userInfo);
        return userInfoRepository.save(userInfo).flatMap(userInfoSearchRepository::save);
    }

    /**
     * Update a userInfo.
     *
     * @param userInfo the entity to save.
     * @return the persisted entity.
     */
    public Mono<UserInfo> update(UserInfo userInfo) {
        LOG.debug("Request to update UserInfo : {}", userInfo);
        return userInfoRepository.save(userInfo).flatMap(userInfoSearchRepository::save);
    }

    /**
     * Partially update a userInfo.
     *
     * @param userInfo the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<UserInfo> partialUpdate(UserInfo userInfo) {
        LOG.debug("Request to partially update UserInfo : {}", userInfo);

        return userInfoRepository
            .findById(userInfo.getId())
            .map(existingUserInfo -> {
                if (userInfo.getUserId() != null) {
                    existingUserInfo.setUserId(userInfo.getUserId());
                }
                if (userInfo.getFirstname() != null) {
                    existingUserInfo.setFirstname(userInfo.getFirstname());
                }
                if (userInfo.getLastname() != null) {
                    existingUserInfo.setLastname(userInfo.getLastname());
                }
                if (userInfo.getAlias() != null) {
                    existingUserInfo.setAlias(userInfo.getAlias());
                }
                if (userInfo.getGender() != null) {
                    existingUserInfo.setGender(userInfo.getGender());
                }
                if (userInfo.getEmail() != null) {
                    existingUserInfo.setEmail(userInfo.getEmail());
                }
                if (userInfo.getPhone() != null) {
                    existingUserInfo.setPhone(userInfo.getPhone());
                }
                if (userInfo.getAddressLine1() != null) {
                    existingUserInfo.setAddressLine1(userInfo.getAddressLine1());
                }
                if (userInfo.getAddressLine2() != null) {
                    existingUserInfo.setAddressLine2(userInfo.getAddressLine2());
                }
                if (userInfo.getCity() != null) {
                    existingUserInfo.setCity(userInfo.getCity());
                }
                if (userInfo.getCountry() != null) {
                    existingUserInfo.setCountry(userInfo.getCountry());
                }
                if (userInfo.getCreatedAt() != null) {
                    existingUserInfo.setCreatedAt(userInfo.getCreatedAt());
                }
                if (userInfo.getUpdatedAt() != null) {
                    existingUserInfo.setUpdatedAt(userInfo.getUpdatedAt());
                }

                return existingUserInfo;
            })
            .flatMap(userInfoRepository::save)
            .flatMap(savedUserInfo -> {
                userInfoSearchRepository.save(savedUserInfo);
                return Mono.just(savedUserInfo);
            });
    }

    /**
     * Get all the userInfos.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<UserInfo> findAll(Pageable pageable) {
        LOG.debug("Request to get all UserInfos");
        return userInfoRepository.findAllBy(pageable);
    }

    /**
     * Returns the number of userInfos available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return userInfoRepository.count();
    }

    /**
     * Returns the number of userInfos available in search repository.
     *
     */
    public Mono<Long> searchCount() {
        return userInfoSearchRepository.count();
    }

    /**
     * Get one userInfo by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<UserInfo> findOne(Long id) {
        LOG.debug("Request to get UserInfo : {}", id);
        return userInfoRepository.findById(id);
    }

    /**
     * Delete the userInfo by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete UserInfo : {}", id);
        return userInfoRepository.deleteById(id).then(userInfoSearchRepository.deleteById(id));
    }

    /**
     * Search for the userInfo corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<UserInfo> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of UserInfos for query {}", query);
        return userInfoSearchRepository.search(query, pageable);
    }
}
