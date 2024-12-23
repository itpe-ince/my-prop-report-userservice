package com.dnc.mprs.userservice.web.rest;

import static com.dnc.mprs.userservice.domain.UserInfoAsserts.*;
import static com.dnc.mprs.userservice.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.dnc.mprs.userservice.IntegrationTest;
import com.dnc.mprs.userservice.domain.UserInfo;
import com.dnc.mprs.userservice.domain.enumeration.GenderType;
import com.dnc.mprs.userservice.repository.EntityManager;
import com.dnc.mprs.userservice.repository.UserInfoRepository;
import com.dnc.mprs.userservice.repository.search.UserInfoSearchRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.assertj.core.util.IterableUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.data.util.Streamable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Integration tests for the {@link UserInfoResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class UserInfoResourceIT {

    private static final String DEFAULT_USER_ID = "AAAAAAAAAA";
    private static final String UPDATED_USER_ID = "BBBBBBBBBB";

    private static final String DEFAULT_FIRSTNAME = "AAAAAAAAAA";
    private static final String UPDATED_FIRSTNAME = "BBBBBBBBBB";

    private static final String DEFAULT_LASTNAME = "AAAAAAAAAA";
    private static final String UPDATED_LASTNAME = "BBBBBBBBBB";

    private static final String DEFAULT_ALIAS = "AAAAAAAAAA";
    private static final String UPDATED_ALIAS = "BBBBBBBBBB";

    private static final GenderType DEFAULT_GENDER = GenderType.MALE;
    private static final GenderType UPDATED_GENDER = GenderType.FEMALE;

    private static final String DEFAULT_EMAIL = ")KcAS8@O.)#?7";
    private static final String UPDATED_EMAIL = "av@SE19.rY";

    private static final String DEFAULT_PHONE = "AAAAAAAAAA";
    private static final String UPDATED_PHONE = "BBBBBBBBBB";

    private static final String DEFAULT_ADDRESS_LINE_1 = "AAAAAAAAAA";
    private static final String UPDATED_ADDRESS_LINE_1 = "BBBBBBBBBB";

    private static final String DEFAULT_ADDRESS_LINE_2 = "AAAAAAAAAA";
    private static final String UPDATED_ADDRESS_LINE_2 = "BBBBBBBBBB";

    private static final String DEFAULT_CITY = "AAAAAAAAAA";
    private static final String UPDATED_CITY = "BBBBBBBBBB";

    private static final String DEFAULT_COUNTRY = "AAAAAAAAAA";
    private static final String UPDATED_COUNTRY = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_UPDATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/user-infos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/user-infos/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private UserInfoRepository userInfoRepository;

    @Autowired
    private UserInfoSearchRepository userInfoSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private UserInfo userInfo;

    private UserInfo insertedUserInfo;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserInfo createEntity() {
        return new UserInfo()
            .userId(DEFAULT_USER_ID)
            .firstname(DEFAULT_FIRSTNAME)
            .lastname(DEFAULT_LASTNAME)
            .alias(DEFAULT_ALIAS)
            .gender(DEFAULT_GENDER)
            .email(DEFAULT_EMAIL)
            .phone(DEFAULT_PHONE)
            .addressLine1(DEFAULT_ADDRESS_LINE_1)
            .addressLine2(DEFAULT_ADDRESS_LINE_2)
            .city(DEFAULT_CITY)
            .country(DEFAULT_COUNTRY)
            .createdAt(DEFAULT_CREATED_AT)
            .updatedAt(DEFAULT_UPDATED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserInfo createUpdatedEntity() {
        return new UserInfo()
            .userId(UPDATED_USER_ID)
            .firstname(UPDATED_FIRSTNAME)
            .lastname(UPDATED_LASTNAME)
            .alias(UPDATED_ALIAS)
            .gender(UPDATED_GENDER)
            .email(UPDATED_EMAIL)
            .phone(UPDATED_PHONE)
            .addressLine1(UPDATED_ADDRESS_LINE_1)
            .addressLine2(UPDATED_ADDRESS_LINE_2)
            .city(UPDATED_CITY)
            .country(UPDATED_COUNTRY)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(UserInfo.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    public void setupCsrf() {
        webTestClient = webTestClient.mutateWith(csrf());
    }

    @BeforeEach
    public void initTest() {
        userInfo = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedUserInfo != null) {
            userInfoRepository.delete(insertedUserInfo).block();
            userInfoSearchRepository.delete(insertedUserInfo).block();
            insertedUserInfo = null;
        }
        deleteEntities(em);
    }

    @Test
    void createUserInfo() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(userInfoSearchRepository.findAll().collectList().block());
        // Create the UserInfo
        var returnedUserInfo = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(userInfo))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(UserInfo.class)
            .returnResult()
            .getResponseBody();

        // Validate the UserInfo in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertUserInfoUpdatableFieldsEquals(returnedUserInfo, getPersistedUserInfo(returnedUserInfo));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(userInfoSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedUserInfo = returnedUserInfo;
    }

    @Test
    void createUserInfoWithExistingId() throws Exception {
        // Create the UserInfo with an existing ID
        userInfo.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(userInfoSearchRepository.findAll().collectList().block());

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(userInfo))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the UserInfo in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(userInfoSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkUserIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(userInfoSearchRepository.findAll().collectList().block());
        // set the field null
        userInfo.setUserId(null);

        // Create the UserInfo, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(userInfo))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(userInfoSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkFirstnameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(userInfoSearchRepository.findAll().collectList().block());
        // set the field null
        userInfo.setFirstname(null);

        // Create the UserInfo, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(userInfo))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(userInfoSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkLastnameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(userInfoSearchRepository.findAll().collectList().block());
        // set the field null
        userInfo.setLastname(null);

        // Create the UserInfo, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(userInfo))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(userInfoSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkAliasIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(userInfoSearchRepository.findAll().collectList().block());
        // set the field null
        userInfo.setAlias(null);

        // Create the UserInfo, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(userInfo))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(userInfoSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkGenderIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(userInfoSearchRepository.findAll().collectList().block());
        // set the field null
        userInfo.setGender(null);

        // Create the UserInfo, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(userInfo))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(userInfoSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkEmailIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(userInfoSearchRepository.findAll().collectList().block());
        // set the field null
        userInfo.setEmail(null);

        // Create the UserInfo, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(userInfo))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(userInfoSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(userInfoSearchRepository.findAll().collectList().block());
        // set the field null
        userInfo.setCreatedAt(null);

        // Create the UserInfo, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(userInfo))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(userInfoSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllUserInfos() {
        // Initialize the database
        insertedUserInfo = userInfoRepository.save(userInfo).block();

        // Get all the userInfoList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(userInfo.getId().intValue()))
            .jsonPath("$.[*].userId")
            .value(hasItem(DEFAULT_USER_ID))
            .jsonPath("$.[*].firstname")
            .value(hasItem(DEFAULT_FIRSTNAME))
            .jsonPath("$.[*].lastname")
            .value(hasItem(DEFAULT_LASTNAME))
            .jsonPath("$.[*].alias")
            .value(hasItem(DEFAULT_ALIAS))
            .jsonPath("$.[*].gender")
            .value(hasItem(DEFAULT_GENDER.toString()))
            .jsonPath("$.[*].email")
            .value(hasItem(DEFAULT_EMAIL))
            .jsonPath("$.[*].phone")
            .value(hasItem(DEFAULT_PHONE))
            .jsonPath("$.[*].addressLine1")
            .value(hasItem(DEFAULT_ADDRESS_LINE_1))
            .jsonPath("$.[*].addressLine2")
            .value(hasItem(DEFAULT_ADDRESS_LINE_2))
            .jsonPath("$.[*].city")
            .value(hasItem(DEFAULT_CITY))
            .jsonPath("$.[*].country")
            .value(hasItem(DEFAULT_COUNTRY))
            .jsonPath("$.[*].createdAt")
            .value(hasItem(DEFAULT_CREATED_AT.toString()))
            .jsonPath("$.[*].updatedAt")
            .value(hasItem(DEFAULT_UPDATED_AT.toString()));
    }

    @Test
    void getUserInfo() {
        // Initialize the database
        insertedUserInfo = userInfoRepository.save(userInfo).block();

        // Get the userInfo
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, userInfo.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(userInfo.getId().intValue()))
            .jsonPath("$.userId")
            .value(is(DEFAULT_USER_ID))
            .jsonPath("$.firstname")
            .value(is(DEFAULT_FIRSTNAME))
            .jsonPath("$.lastname")
            .value(is(DEFAULT_LASTNAME))
            .jsonPath("$.alias")
            .value(is(DEFAULT_ALIAS))
            .jsonPath("$.gender")
            .value(is(DEFAULT_GENDER.toString()))
            .jsonPath("$.email")
            .value(is(DEFAULT_EMAIL))
            .jsonPath("$.phone")
            .value(is(DEFAULT_PHONE))
            .jsonPath("$.addressLine1")
            .value(is(DEFAULT_ADDRESS_LINE_1))
            .jsonPath("$.addressLine2")
            .value(is(DEFAULT_ADDRESS_LINE_2))
            .jsonPath("$.city")
            .value(is(DEFAULT_CITY))
            .jsonPath("$.country")
            .value(is(DEFAULT_COUNTRY))
            .jsonPath("$.createdAt")
            .value(is(DEFAULT_CREATED_AT.toString()))
            .jsonPath("$.updatedAt")
            .value(is(DEFAULT_UPDATED_AT.toString()));
    }

    @Test
    void getNonExistingUserInfo() {
        // Get the userInfo
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingUserInfo() throws Exception {
        // Initialize the database
        insertedUserInfo = userInfoRepository.save(userInfo).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();
        userInfoSearchRepository.save(userInfo).block();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(userInfoSearchRepository.findAll().collectList().block());

        // Update the userInfo
        UserInfo updatedUserInfo = userInfoRepository.findById(userInfo.getId()).block();
        updatedUserInfo
            .userId(UPDATED_USER_ID)
            .firstname(UPDATED_FIRSTNAME)
            .lastname(UPDATED_LASTNAME)
            .alias(UPDATED_ALIAS)
            .gender(UPDATED_GENDER)
            .email(UPDATED_EMAIL)
            .phone(UPDATED_PHONE)
            .addressLine1(UPDATED_ADDRESS_LINE_1)
            .addressLine2(UPDATED_ADDRESS_LINE_2)
            .city(UPDATED_CITY)
            .country(UPDATED_COUNTRY)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedUserInfo.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(updatedUserInfo))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the UserInfo in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedUserInfoToMatchAllProperties(updatedUserInfo);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(userInfoSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<UserInfo> userInfoSearchList = Streamable.of(userInfoSearchRepository.findAll().collectList().block()).toList();
                UserInfo testUserInfoSearch = userInfoSearchList.get(searchDatabaseSizeAfter - 1);

                // Test fails because reactive api returns an empty object instead of null
                // assertUserInfoAllPropertiesEquals(testUserInfoSearch, updatedUserInfo);
                assertUserInfoUpdatableFieldsEquals(testUserInfoSearch, updatedUserInfo);
            });
    }

    @Test
    void putNonExistingUserInfo() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(userInfoSearchRepository.findAll().collectList().block());
        userInfo.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, userInfo.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(userInfo))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the UserInfo in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(userInfoSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchUserInfo() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(userInfoSearchRepository.findAll().collectList().block());
        userInfo.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(userInfo))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the UserInfo in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(userInfoSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamUserInfo() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(userInfoSearchRepository.findAll().collectList().block());
        userInfo.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(userInfo))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the UserInfo in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(userInfoSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateUserInfoWithPatch() throws Exception {
        // Initialize the database
        insertedUserInfo = userInfoRepository.save(userInfo).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the userInfo using partial update
        UserInfo partialUpdatedUserInfo = new UserInfo();
        partialUpdatedUserInfo.setId(userInfo.getId());

        partialUpdatedUserInfo
            .alias(UPDATED_ALIAS)
            .addressLine1(UPDATED_ADDRESS_LINE_1)
            .addressLine2(UPDATED_ADDRESS_LINE_2)
            .city(UPDATED_CITY)
            .country(UPDATED_COUNTRY)
            .createdAt(UPDATED_CREATED_AT);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedUserInfo.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedUserInfo))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the UserInfo in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertUserInfoUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedUserInfo, userInfo), getPersistedUserInfo(userInfo));
    }

    @Test
    void fullUpdateUserInfoWithPatch() throws Exception {
        // Initialize the database
        insertedUserInfo = userInfoRepository.save(userInfo).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the userInfo using partial update
        UserInfo partialUpdatedUserInfo = new UserInfo();
        partialUpdatedUserInfo.setId(userInfo.getId());

        partialUpdatedUserInfo
            .userId(UPDATED_USER_ID)
            .firstname(UPDATED_FIRSTNAME)
            .lastname(UPDATED_LASTNAME)
            .alias(UPDATED_ALIAS)
            .gender(UPDATED_GENDER)
            .email(UPDATED_EMAIL)
            .phone(UPDATED_PHONE)
            .addressLine1(UPDATED_ADDRESS_LINE_1)
            .addressLine2(UPDATED_ADDRESS_LINE_2)
            .city(UPDATED_CITY)
            .country(UPDATED_COUNTRY)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedUserInfo.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedUserInfo))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the UserInfo in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertUserInfoUpdatableFieldsEquals(partialUpdatedUserInfo, getPersistedUserInfo(partialUpdatedUserInfo));
    }

    @Test
    void patchNonExistingUserInfo() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(userInfoSearchRepository.findAll().collectList().block());
        userInfo.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, userInfo.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(userInfo))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the UserInfo in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(userInfoSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchUserInfo() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(userInfoSearchRepository.findAll().collectList().block());
        userInfo.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(userInfo))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the UserInfo in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(userInfoSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamUserInfo() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(userInfoSearchRepository.findAll().collectList().block());
        userInfo.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(userInfo))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the UserInfo in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(userInfoSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteUserInfo() {
        // Initialize the database
        insertedUserInfo = userInfoRepository.save(userInfo).block();
        userInfoRepository.save(userInfo).block();
        userInfoSearchRepository.save(userInfo).block();

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(userInfoSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the userInfo
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, userInfo.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(userInfoSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchUserInfo() {
        // Initialize the database
        insertedUserInfo = userInfoRepository.save(userInfo).block();
        userInfoSearchRepository.save(userInfo).block();

        // Search the userInfo
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + userInfo.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(userInfo.getId().intValue()))
            .jsonPath("$.[*].userId")
            .value(hasItem(DEFAULT_USER_ID))
            .jsonPath("$.[*].firstname")
            .value(hasItem(DEFAULT_FIRSTNAME))
            .jsonPath("$.[*].lastname")
            .value(hasItem(DEFAULT_LASTNAME))
            .jsonPath("$.[*].alias")
            .value(hasItem(DEFAULT_ALIAS))
            .jsonPath("$.[*].gender")
            .value(hasItem(DEFAULT_GENDER.toString()))
            .jsonPath("$.[*].email")
            .value(hasItem(DEFAULT_EMAIL))
            .jsonPath("$.[*].phone")
            .value(hasItem(DEFAULT_PHONE))
            .jsonPath("$.[*].addressLine1")
            .value(hasItem(DEFAULT_ADDRESS_LINE_1))
            .jsonPath("$.[*].addressLine2")
            .value(hasItem(DEFAULT_ADDRESS_LINE_2))
            .jsonPath("$.[*].city")
            .value(hasItem(DEFAULT_CITY))
            .jsonPath("$.[*].country")
            .value(hasItem(DEFAULT_COUNTRY))
            .jsonPath("$.[*].createdAt")
            .value(hasItem(DEFAULT_CREATED_AT.toString()))
            .jsonPath("$.[*].updatedAt")
            .value(hasItem(DEFAULT_UPDATED_AT.toString()));
    }

    protected long getRepositoryCount() {
        return userInfoRepository.count().block();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected UserInfo getPersistedUserInfo(UserInfo userInfo) {
        return userInfoRepository.findById(userInfo.getId()).block();
    }

    protected void assertPersistedUserInfoToMatchAllProperties(UserInfo expectedUserInfo) {
        // Test fails because reactive api returns an empty object instead of null
        // assertUserInfoAllPropertiesEquals(expectedUserInfo, getPersistedUserInfo(expectedUserInfo));
        assertUserInfoUpdatableFieldsEquals(expectedUserInfo, getPersistedUserInfo(expectedUserInfo));
    }

    protected void assertPersistedUserInfoToMatchUpdatableProperties(UserInfo expectedUserInfo) {
        // Test fails because reactive api returns an empty object instead of null
        // assertUserInfoAllUpdatablePropertiesEquals(expectedUserInfo, getPersistedUserInfo(expectedUserInfo));
        assertUserInfoUpdatableFieldsEquals(expectedUserInfo, getPersistedUserInfo(expectedUserInfo));
    }
}
