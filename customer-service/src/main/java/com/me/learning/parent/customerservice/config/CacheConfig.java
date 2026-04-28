package com.me.learning.parent.customerservice.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Author   : Prabakaran Ramu
 * User     : ramup
 * Date     : 20/04/2026
 * Usage    : Caching configuration for the Customer Service
 * Since    : Version 1.0
 */
@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager () {
        return new ConcurrentMapCacheManager (
                "countries", "country",
                "cities", "city",
                "addresses", "address",
                "stores", "store",
                "staffList", "staff",
                "customers", "customer",
                "rentals", "rental",
                "payments", "payment"
        );
    }
}

