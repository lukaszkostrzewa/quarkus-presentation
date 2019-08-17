package com.example;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import java.text.MessageFormat;

/**
 * @author Lukasz Kostrzewa (SG0221165)
 * @since Aug 17, 2019
 */
@ApplicationScoped
class GreetingService {

    @ConfigProperty(name = "greeting")
    private String greeting;

    String greeting(String name) {
        return MessageFormat.format(greeting, name);
    }
}
