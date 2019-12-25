package com.ripple.cloudshare.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "com.ripple.cloudshare.security")
public class SecurityProperties {

    private String secret = "S3CReT";
    private Long tokenValidityInMs = 1111L;

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public Long getTokenValidityInMs() {
        return tokenValidityInMs;
    }

    public void setTokenValidityInMs(Long tokenValidityInMs) {
        this.tokenValidityInMs = tokenValidityInMs;
    }
}
