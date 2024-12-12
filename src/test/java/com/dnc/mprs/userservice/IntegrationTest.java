package com.dnc.mprs.userservice;

import com.dnc.mprs.userservice.config.AsyncSyncConfiguration;
import com.dnc.mprs.userservice.config.EmbeddedElasticsearch;
import com.dnc.mprs.userservice.config.EmbeddedKafka;
import com.dnc.mprs.userservice.config.EmbeddedRedis;
import com.dnc.mprs.userservice.config.EmbeddedSQL;
import com.dnc.mprs.userservice.config.JacksonConfiguration;
import com.dnc.mprs.userservice.config.TestSecurityConfiguration;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Base composite annotation for integration tests.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(
    classes = { UserserviceApp.class, JacksonConfiguration.class, AsyncSyncConfiguration.class, TestSecurityConfiguration.class }
)
@EmbeddedRedis
@EmbeddedElasticsearch
@EmbeddedSQL
@EmbeddedKafka
public @interface IntegrationTest {
}
