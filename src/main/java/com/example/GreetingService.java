package com.example;

import javax.enterprise.context.ApplicationScoped;

/**
 * @author Lukasz Kostrzewa (SG0221165)
 * @since Aug 17, 2019
 */
@ApplicationScoped
class GreetingService {

    String greeting(String name) {
        return "Hello, " + name;
    }
}
