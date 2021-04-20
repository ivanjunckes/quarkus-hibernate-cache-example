package org.example;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.logging.Logger;

@Path("/users")
public class UserResource {

    Logger logger = Logger.getLogger(UserResource.class.getName());

    @Inject
    EntityManager entityManager;

    @Transactional
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<User> hello() {
        User user1 = new User("Tiago");
        User user2 = new User("João");
        User user3 = new User("Ivan");
        entityManager.persist(user1);
        entityManager.persist(user2);
        entityManager.persist(user3);

        logger.info("FIRST QUERY NO CACHE");
        final List usersWithoutCache = entityManager.createQuery("from User")
                                                    .setHint("org.hibernate.cacheable", Boolean.TRUE)
                                                    .getResultList();
        logger.info("SECOND QUERY CACHE");
        final List usersWithCache = entityManager.createQuery("from User")
                                                 .setHint("org.hibernate.cacheable", Boolean.TRUE)
                                                 .getResultList();
        return usersWithCache;
    }
}