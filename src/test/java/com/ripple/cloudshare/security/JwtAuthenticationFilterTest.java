package com.ripple.cloudshare.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    JwtTokenProvider tokenProvider;

    @Mock
    SecurityUserDetailsService securityUserDetailsService;

    @InjectMocks
    JwtAuthenticationFilter filter;

    @Mock
    HttpServletRequest request;

    @Mock
    HttpServletResponse response;

    @Mock
    FilterChain filterChain;

    @Mock
    SecurityUser securityUser;

    @Mock
    SecurityContext securityContext;

    @Captor
    ArgumentCaptor<UsernamePasswordAuthenticationToken> authenticationCaptor;

    @Test
    void doFilterInternal() throws ServletException, IOException {
        when(request.getHeader(eq("Authorization"))).thenReturn("Bearer VALID_TOKEN");
        when(tokenProvider.validateToken(eq("VALID_TOKEN"))).thenReturn(true);
        when(tokenProvider.getUserIdFromJWT(eq("VALID_TOKEN"))).thenReturn(1L);
        when(securityUserDetailsService.loadUserById(eq(1L))).thenReturn(securityUser);

        try (MockedStatic<SecurityContextHolder> utilities = Mockito.mockStatic(SecurityContextHolder.class)) {
            utilities.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            filter.doFilterInternal(request, response, filterChain);

            verify(securityContext, times(1)).setAuthentication(authenticationCaptor.capture());
            UsernamePasswordAuthenticationToken capturedAuth = authenticationCaptor.getValue();
            assertSame(securityUser, capturedAuth.getPrincipal());

            verify(filterChain).doFilter(eq(request), eq(response));
        }

    }
}