package com.me.learning.parent.paymentservice.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Author   : Prabakaran Ramu
 * User     : ramup
 * Usage    : Caching configuration â€” add one singular + one plural name per entity
 */
@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager () {
        // TODO: replace with real entity cache names
        return new ConcurrentMapCacheManager ("items", "item");
    }
}
