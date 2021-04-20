package org.example;

import io.quarkus.test.junit.QuarkusTest;
import org.hibernate.Session;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.util.Assert;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.ws.rs.core.MediaType;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static io.restassured.RestAssured.given;

@QuarkusTest
public class UserResourceTest {
    @Inject
    EntityManager entityManager;

    @Test
    public void testUsersWithCacheEndpoint() {
        final List users = given()
            .accept(MediaType.APPLICATION_JSON)
            .when().get("/users")
            .then()
            .statusCode(200)
            .extract().as(List.class);

        Assert.equals(3, users.size());

        final Session session = entityManager.unwrap(Session.class);
        final long secondLevelCacheHitCount = session.getSessionFactory().getStatistics().getSecondLevelCacheHitCount();
        Assert.equals(1, secondLevelCacheHitCount);
    }

    @Test
    public void testQuarkusCacheUsers() {
        final Instant start = Instant.now();
        List users = given()
            .accept(MediaType.APPLICATION_JSON)
            .when().get("/users/quarkus?usage=MEETING")
            .then()
            .statusCode(200)
            .extract().as(List.class);

        final Duration noCache = Duration.between(start, Instant.now());
        System.out.println("TIME NO CACHE: " + noCache.getSeconds() + " Seconds.");

        Assert.equals(3, users.size());
        final Instant startCache = Instant.now();
        users = given()
            .accept(MediaType.APPLICATION_JSON)
            .when().get("/users/quarkus?usage=MEETING")
            .then()
            .statusCode(200)
            .extract().as(List.class);

        final Duration cache = Duration.between(startCache, Instant.now());
        System.out.println("TIME WITH CACHE: " + cache.getSeconds() + " Seconds.");


        Assert.equals(3, users.size());
    }
}