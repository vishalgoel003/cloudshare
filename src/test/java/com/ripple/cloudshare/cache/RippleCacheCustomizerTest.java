package com.ripple.cloudshare.cache;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RippleCacheCustomizerTest {

    @Mock
    ConcurrentMapCacheManager cacheManager;

    @Captor
    ArgumentCaptor<List<String>> cacheNameCaptor;

    @InjectMocks
    RippleCacheCustomizer rippleCacheCustomizer;

    @Test
    void customize() {
        rippleCacheCustomizer.customize(cacheManager);

        verify(cacheManager).setCacheNames(cacheNameCaptor.capture());
        List<String> cacheNames = cacheNameCaptor.getValue();

        assertEquals(2, cacheNames.size());
        assertTrue(cacheNames.contains("users"));
        assertTrue(cacheNames.contains("user_by_id"));
    }
}