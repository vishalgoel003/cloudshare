package com.ripple.cloudshare.controller;

import com.ripple.cloudshare.data.dao.UserDAOService;
import com.ripple.cloudshare.data.entity.User;
import com.ripple.cloudshare.dto.request.SignInRequest;
import com.ripple.cloudshare.dto.request.SignUpRequest;
import com.ripple.cloudshare.dto.response.SignInResponse;
import com.ripple.cloudshare.dto.response.SignUpResponse;
import com.ripple.cloudshare.security.JwtTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("auth")
public class AuthController {

    private static final String CLASS_NAME = "AuthController";

    private final Logger logger;
    private final UserDAOService userDAOService;

    private final AuthenticationManager authenticationManager;

    private final JwtTokenProvider tokenProvider;

    private final PasswordEncoder passwordEncoder;

    public AuthController(UserDAOService userDAOService, AuthenticationManager authenticationManager,
                          JwtTokenProvider jwtTokenProvider, PasswordEncoder passwordEncoder) {
        this.userDAOService = userDAOService;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = jwtTokenProvider;
        this.passwordEncoder = passwordEncoder;
        this.logger = LoggerFactory.getLogger(CLASS_NAME);
    }

    @PostMapping("/sign-up")
    public SignUpResponse signUp(@Valid @RequestBody SignUpRequest signUpRequest) {
        logger.info("Request received: " + signUpRequest);
        //encrypting password
        signUpRequest.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        return SignUpResponse.fromUserEntity(userDAOService.createUser(signUpRequest));
    }

    @PostMapping("/sign-in")
    public SignInResponse signIn(@Valid @RequestBody SignInRequest signIpRequest) {
        logger.info("Request received: " + signIpRequest);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        signIpRequest.getEmail(),
                        signIpRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.generateToken(authentication);
        return SignInResponse.fromAuthorizationToken(String.format("Bearer %s", jwt));
    }

}
