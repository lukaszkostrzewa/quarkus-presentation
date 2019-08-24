package com.example;

import lombok.Data;

import javax.json.bind.annotation.JsonbProperty;

/**
 * @author Lukasz Kostrzewa (SG0221165)
 * @since Aug 24, 2019
 */
@Data
public class Rating {

    double imdbRating;
    String imdbVotes;
    @JsonbProperty("Title")
    String title;
}
