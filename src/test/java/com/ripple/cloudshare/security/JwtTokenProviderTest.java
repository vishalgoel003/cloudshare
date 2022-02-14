package com.ripple.cloudshare.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtTokenProviderTest {

    @Mock
    SecurityProperties securityProperties;

    @InjectMocks
    JwtTokenProvider jwtTokenProvider;

    @Mock
    Authentication authentication;

    @Mock
    SecurityUser securityUser;

    @Test
    void testLifeCycle() {
        when(securityProperties.getSecret()).thenReturn("S3CReT");
        when(securityProperties.getTokenValidityInMs()).thenReturn(10000L);
        when(authentication.getPrincipal()).thenReturn(securityUser);
        when(securityUser.getId()).thenReturn(1L);

        String token = jwtTokenProvider.generateToken(authentication);

        assertNotNull(token);
        assertTrue(token.length() > 0);

        boolean validationResult = jwtTokenProvider.validateToken(token);

        assertTrue(validationResult);

        Long idFromToken = jwtTokenProvider.getUserIdFromJWT(token);

        assertEquals(1L, idFromToken);
    }

}