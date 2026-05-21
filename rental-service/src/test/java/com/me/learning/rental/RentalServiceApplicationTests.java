package com.me.learning.rental;

import org.junit.jupiter.api.Test;

import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
class RentalServiceApplicationTests {

    @Test
    void contextLoads () {
        assertDoesNotThrow (() -> {}, "The Spring application context should load without exceptions");
    }

}

