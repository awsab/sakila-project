package com.me.learning.catalog;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CatalogServiceApplicationTests {

    @Test
    void contextLoads () {
        Assertions.assertDoesNotThrow (() -> {}, "The Spring application context should load without exceptions");
    }

}
