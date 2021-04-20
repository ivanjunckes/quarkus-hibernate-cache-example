package org.example;

import io.quarkus.test.junit.QuarkusTest;
import org.hibernate.Session;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.util.Assert;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.ws.rs.core.MediaType;

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
}