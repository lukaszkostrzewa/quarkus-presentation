package com.example;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

/**
 * @author Lukasz Kostrzewa (SG0221165)
 * @since Aug 24, 2019
 */
@Path("/")
@RegisterRestClient
public interface RatingService {

    @GET
    @Produces("application/json")
    Rating getByTitle(@QueryParam("t") String title);
}
