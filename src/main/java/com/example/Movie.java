package com.example;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * @author Lukasz Kostrzewa (SG0221165)
 * @since Aug 17, 2019
 */
@Data
@Entity
public class Movie {

    @Id
    @GeneratedValue
    private Long id;
    private String title;
    private String genre;
    private int year;
}
