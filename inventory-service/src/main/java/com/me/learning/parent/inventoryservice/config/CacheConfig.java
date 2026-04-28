package com.me.learning.parent.inventoryservice.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Author   : Prabakaran Ramu
 * User     : ramup
 * Date     : 11/03/2026
 * Usage    : Caching configuration for application
 * Since    : Version 1.0
 */
@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager () {
        return new ConcurrentMapCacheManager (
                "actors",
                "actor",
                "films",
                "film",
                "categories",
                "category",
                "languages",
                "language"
        );
    }
}

