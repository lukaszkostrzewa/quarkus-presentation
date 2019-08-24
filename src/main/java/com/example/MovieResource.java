package com.example;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

import static com.example.Movie.YEAR;

/**
 * @author Lukasz Kostrzewa (SG0221165)
 * @since Aug 17, 2019
 */
@Slf4j
@Path("/movies")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MovieResource {

    @Inject
    @RestClient
    RatingService ratingService;

    private AtomicLong counter = new AtomicLong(0);

    @GET
    public List<Movie> movies(@QueryParam(YEAR) Integer year) {
        return Optional.ofNullable(year)
            .map(Movie::listByYear)
            .orElseGet(Movie::listAll);
    }

    @POST
    @Transactional
    public void save(Movie movie) {
        movie.persist();
    }

    @GET
    @Path("/{id}/rating")
    @Retry(maxRetries = 4)
    public Rating rating(@PathParam("id") Long id) {
        final Long invocationNumber = counter.getAndIncrement();
        maybeFail(invocationNumber);
        log.info("Rating invocation #{} succeeded", invocationNumber);

        String title = Movie.<Movie>findById(id).getTitle();
        return ratingService.getByTitle(title);
    }

    private void maybeFail(Long invocationNumber) {
        if (new Random().nextBoolean()) {
            log.error("Rating invocation #{} failed", invocationNumber);
            throw new RuntimeException("Could not fetch data.");
        }
    }
}