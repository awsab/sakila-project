package com.me.learning.framework;

/**
 * Marker type for the Sakila framework package.
 *
 * <p>This module is a reusable library, not a runnable Spring Boot application.
 * Consumers should import the artifact as a dependency and let
 * {@link SakilaFrameworkAutoConfiguration} register framework beans.
 */
public final class SakilaFrameworkApplication {

    private SakilaFrameworkApplication() {
    }

}

