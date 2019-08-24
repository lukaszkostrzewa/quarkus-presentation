package com.example;

import javax.enterprise.context.ApplicationScoped;
import java.util.Collections;
import java.util.List;

/**
 * @author Lukasz Kostrzewa (SG0221165)
 * @since Aug 24, 2019
 */
@ApplicationScoped
class RecommendationService {

    List<Movie> getRecommendations(Long id) {
        List<Movie> recommendations = Movie.list("id != ?1", id);
        Collections.shuffle(recommendations);
        return recommendations.subList(0, 2);
    }
}
