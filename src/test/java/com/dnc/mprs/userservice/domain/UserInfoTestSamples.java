package com.dnc.mprs.userservice.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class UserInfoTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static UserInfo getUserInfoSample1() {
        return new UserInfo()
            .id(1L)
            .userId("userId1")
            .firstname("firstname1")
            .lastname("lastname1")
            .alias("alias1")
            .email("email1")
            .phone("phone1")
            .addressLine1("addressLine11")
            .addressLine2("addressLine21")
            .city("city1")
            .country("country1");
    }

    public static UserInfo getUserInfoSample2() {
        return new UserInfo()
            .id(2L)
            .userId("userId2")
            .firstname("firstname2")
            .lastname("lastname2")
            .alias("alias2")
            .email("email2")
            .phone("phone2")
            .addressLine1("addressLine12")
            .addressLine2("addressLine22")
            .city("city2")
            .country("country2");
    }

    public static UserInfo getUserInfoRandomSampleGenerator() {
        return new UserInfo()
            .id(longCount.incrementAndGet())
            .userId(UUID.randomUUID().toString())
            .firstname(UUID.randomUUID().toString())
            .lastname(UUID.randomUUID().toString())
            .alias(UUID.randomUUID().toString())
            .email(UUID.randomUUID().toString())
            .phone(UUID.randomUUID().toString())
            .addressLine1(UUID.randomUUID().toString())
            .addressLine2(UUID.randomUUID().toString())
            .city(UUID.randomUUID().toString())
            .country(UUID.randomUUID().toString());
    }
}
