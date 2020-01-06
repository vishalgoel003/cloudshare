package com.ripple.cloudshare.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizer;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class RippleCacheCustomizer implements CacheManagerCustomizer<ConcurrentMapCacheManager> {

    private final Logger logger = LoggerFactory.getLogger("RippleCacheCustomizer");

    @Override
    public void customize(ConcurrentMapCacheManager cacheManager) {
        logger.info("Setting up caches");
        cacheManager.setCacheNames(Arrays.asList("users", "user_by_id"));
    }

}