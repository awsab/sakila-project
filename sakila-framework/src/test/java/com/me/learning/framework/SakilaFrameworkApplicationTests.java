package com.me.learning.framework;

import org.junit.jupiter.api.Test;

import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * Integration smoke test: verifies that the sakila-framework auto-configuration
 * loads without errors when the framework is on the classpath.
 */
@SpringBootTest(classes = TestFrameworkApplication.class)
class SakilaFrameworkApplicationTests {

    @Test
    void contextLoads() {
        assertDoesNotThrow(() -> {}, "The Spring application context should load without exceptions");
    }

}

