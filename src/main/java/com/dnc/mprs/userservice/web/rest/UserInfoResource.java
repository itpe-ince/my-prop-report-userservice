package com.dnc.mprs.userservice.web.rest;

import com.dnc.mprs.userservice.domain.UserInfo;
import com.dnc.mprs.userservice.repository.UserInfoRepository;
import com.dnc.mprs.userservice.service.UserInfoService;
import com.dnc.mprs.userservice.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.ForwardedHeaderUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.dnc.mprs.userservice.domain.UserInfo}.
 */
@RestController
@RequestMapping("/api/user-infos")
public class UserInfoResource {

    private static final Logger LOG = LoggerFactory.getLogger(UserInfoResource.class);

    private static final String ENTITY_NAME = "userserviceUserInfo";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final UserInfoService userInfoService;

    private final UserInfoRepository userInfoRepository;

    public UserInfoResource(UserInfoService userInfoService, UserInfoRepository userInfoRepository) {
        this.userInfoService = userInfoService;
        this.userInfoRepository = userInfoRepository;
    }

    /**
     * {@code POST  /user-infos} : Create a new userInfo.
     *
     * @param userInfo the userInfo to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new userInfo, or with status {@code 400 (Bad Request)} if the userInfo has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<UserInfo>> createUserInfo(@Valid @RequestBody UserInfo userInfo) throws URISyntaxException {
        LOG.debug("REST request to save UserInfo : {}", userInfo);
        if (userInfo.getId() != null) {
            throw new BadRequestAlertException("A new userInfo cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return userInfoService
            .save(userInfo)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/user-infos/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /user-infos/:id} : Updates an existing userInfo.
     *
     * @param id the id of the userInfo to save.
     * @param userInfo the userInfo to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userInfo,
     * or with status {@code 400 (Bad Request)} if the userInfo is not valid,
     * or with status {@code 500 (Internal Server Error)} if the userInfo couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<UserInfo>> updateUserInfo(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody UserInfo userInfo
    ) throws URISyntaxException {
        LOG.debug("REST request to update UserInfo : {}, {}", id, userInfo);
        if (userInfo.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, userInfo.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return userInfoRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return userInfoService
                    .update(userInfo)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /user-infos/:id} : Partial updates given fields of an existing userInfo, field will ignore if it is null
     *
     * @param id the id of the userInfo to save.
     * @param userInfo the userInfo to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userInfo,
     * or with status {@code 400 (Bad Request)} if the userInfo is not valid,
     * or with status {@code 404 (Not Found)} if the userInfo is not found,
     * or with status {@code 500 (Internal Server Error)} if the userInfo couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<UserInfo>> partialUpdateUserInfo(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody UserInfo userInfo
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update UserInfo partially : {}, {}", id, userInfo);
        if (userInfo.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, userInfo.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return userInfoRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<UserInfo> result = userInfoService.partialUpdate(userInfo);

                return result
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(res ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, res.getId().toString()))
                            .body(res)
                    );
            });
    }

    /**
     * {@code GET  /user-infos} : get all the userInfos.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of userInfos in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<UserInfo>>> getAllUserInfos(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        LOG.debug("REST request to get a page of UserInfos");
        return userInfoService
            .countAll()
            .zipWith(userInfoService.findAll(pageable).collectList())
            .map(countWithEntities ->
                ResponseEntity.ok()
                    .headers(
                        PaginationUtil.generatePaginationHttpHeaders(
                            ForwardedHeaderUtils.adaptFromForwardedHeaders(request.getURI(), request.getHeaders()),
                            new PageImpl<>(countWithEntities.getT2(), pageable, countWithEntities.getT1())
                        )
                    )
                    .body(countWithEntities.getT2())
            );
    }

    /**
     * {@code GET  /user-infos/:id} : get the "id" userInfo.
     *
     * @param id the id of the userInfo to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the userInfo, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<UserInfo>> getUserInfo(@PathVariable("id") Long id) {
        LOG.debug("REST request to get UserInfo : {}", id);
        Mono<UserInfo> userInfo = userInfoService.findOne(id);
        return ResponseUtil.wrapOrNotFound(userInfo);
    }

    /**
     * {@code DELETE  /user-infos/:id} : delete the "id" userInfo.
     *
     * @param id the id of the userInfo to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteUserInfo(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete UserInfo : {}", id);
        return userInfoService
            .delete(id)
            .then(
                Mono.just(
                    ResponseEntity.noContent()
                        .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                        .build()
                )
            );
    }

    /**
     * {@code SEARCH  /user-infos/_search?query=:query} : search for the userInfo corresponding
     * to the query.
     *
     * @param query the query of the userInfo search.
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public Mono<ResponseEntity<Flux<UserInfo>>> searchUserInfos(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        LOG.debug("REST request to search for a page of UserInfos for query {}", query);
        return userInfoService
            .searchCount()
            .map(total -> new PageImpl<>(new ArrayList<>(), pageable, total))
            .map(page ->
                PaginationUtil.generatePaginationHttpHeaders(
                    ForwardedHeaderUtils.adaptFromForwardedHeaders(request.getURI(), request.getHeaders()),
                    page
                )
            )
            .map(headers -> ResponseEntity.ok().headers(headers).body(userInfoService.search(query, pageable)));
    }
}
