package com.ripple.cloudshare;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableCaching
@EnableJpaAuditing
public class ApplicationConfig {
}
