package com.example;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.List;

/**
 * @author Lukasz Kostrzewa (SG0221165)
 * @since Aug 17, 2019
 */
@ApplicationScoped
public class MovieService {
    @Inject
    EntityManager em;

    List<Movie> all() {
        return em.createQuery("SELECT e FROM Movie e").getResultList();
    }

    @Transactional
    void save(Movie movie) {
        em.persist(movie);
    }
}