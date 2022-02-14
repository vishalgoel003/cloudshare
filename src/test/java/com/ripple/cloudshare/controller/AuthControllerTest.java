package com.ripple.cloudshare.controller;

import com.ripple.cloudshare.data.dao.UserDAOService;
import com.ripple.cloudshare.data.entity.User;
import com.ripple.cloudshare.dto.request.SignInRequest;
import com.ripple.cloudshare.dto.request.SignUpRequest;
import com.ripple.cloudshare.dto.response.SignInResponse;
import com.ripple.cloudshare.dto.response.SignUpResponse;
import com.ripple.cloudshare.security.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import static com.ripple.cloudshare.controller.UserControllerTest.TEST_EMAIL;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    public static final String SECRET_PASSWORD = "$EcReT";

    @InjectMocks
    AuthController controller;

    @Mock
    UserDAOService userDAOService;

    @Mock
    AuthenticationManager authenticationManager;

    @Mock
    JwtTokenProvider tokenProvider;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    SignUpRequest mockSignUpRequest;

    @Mock
    SignInRequest signIpRequest;

    @Mock
    Authentication authentication;

    @Captor
    ArgumentCaptor<UsernamePasswordAuthenticationToken> authCaptor;

    @Mock
    User mockPersistedUser;

    @Test
    void signUp() {
        when(mockSignUpRequest.getPassword()).thenReturn(SECRET_PASSWORD);
        when(passwordEncoder.encode(eq(SECRET_PASSWORD))).thenReturn("ENCODED");
        when(userDAOService.createUser(eq(mockSignUpRequest))).thenReturn(mockPersistedUser);
        when(mockPersistedUser.getId()).thenReturn(1l);
        SignUpResponse response = controller.signUp(mockSignUpRequest);

        verify(userDAOService, times(1)).createUser(any());
        verify(userDAOService).createUser(eq(mockSignUpRequest));
        assertEquals(1L, response.getId());
        assertEquals("/users/" + 1L, response.getPath());
        verify(passwordEncoder, times(1)).encode(eq(SECRET_PASSWORD));
        verify(mockSignUpRequest, times(1)).setPassword(eq("ENCODED"));
    }

    @Test
    void signIn() {
        when(signIpRequest.getEmail()).thenReturn(TEST_EMAIL);
        when(signIpRequest.getPassword()).thenReturn(SECRET_PASSWORD);
        when(authenticationManager.authenticate(authCaptor.capture())).thenReturn(authentication);
        when(tokenProvider.generateToken(eq(authentication))).thenReturn("TEST_JWT");

        SignInResponse response = controller.signIn(signIpRequest);

        assertEquals("Bearer TEST_JWT", response.getAuthorizationToken());
        assertEquals(TEST_EMAIL, authCaptor.getValue().getPrincipal());
        assertEquals(SECRET_PASSWORD, authCaptor.getValue().getCredentials());
    }
}