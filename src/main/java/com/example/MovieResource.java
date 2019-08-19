package com.example;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * @author Lukasz Kostrzewa (SG0221165)
 * @since Aug 17, 2019
 */
@Path("/movies")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MovieResource {

    @Inject
    MovieService movieService;

    @GET
    public List<Movie> movies() {
        return movieService.all();
    }

    @POST
    public void save(Movie movie) {
        movieService.save(movie);
    }
}