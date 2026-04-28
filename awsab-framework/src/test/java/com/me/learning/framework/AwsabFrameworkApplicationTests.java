package com.me.learning.framework;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Integration smoke test: verifies that the awsab-framework auto-configuration
 * loads without errors when the framework is on the classpath.
 */
@SpringBootTest(classes = TestFrameworkApplication.class)
class AwsabFrameworkApplicationTests {

    @Test
    void contextLoads() {
        Assertions.assertDoesNotThrow(() -> "The Spring application context should load without exceptions");
    }

}


