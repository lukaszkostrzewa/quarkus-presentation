package com.example;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.panache.common.Sort;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import java.util.List;

/**
 * @author Lukasz Kostrzewa (SG0221165)
 * @since Aug 17, 2019
 */
@Data
@Entity
@EqualsAndHashCode(callSuper = true)
public class Movie extends PanacheEntity {

    static final String YEAR = "year";
    static final Sort SORT_BY_YEAR = Sort.by(YEAR);

    public String title;
    public String genre;
    public int year;

    public static List<Movie> listAll() {
        return listAll(SORT_BY_YEAR);
    }

    static List<Movie> listByYear(int year) {
        return list(YEAR, year);
    }
}
