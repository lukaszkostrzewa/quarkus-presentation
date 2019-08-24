package com.example;

import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Optional;

import static com.example.Movie.YEAR;

/**
 * @author Lukasz Kostrzewa (SG0221165)
 * @since Aug 17, 2019
 */
@Path("/movies")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MovieResource {

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
}