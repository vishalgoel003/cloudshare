package com.ripple.cloudshare;

import com.ripple.cloudshare.exception.handler.RippleAsyncExceptionHandler;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableAsync
@EnableCaching
@EnableJpaAuditing
public class ApplicationConfig implements AsyncConfigurer {

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new RippleAsyncExceptionHandler();
    }

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate template = new RestTemplate();

        template.setRequestFactory(new HttpComponentsClientHttpRequestFactory());

        return template;
    }

}
