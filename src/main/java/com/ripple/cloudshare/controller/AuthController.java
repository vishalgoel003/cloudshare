package com.ripple.cloudshare.controller;

import com.ripple.cloudshare.data.dao.UserDAOService;
import com.ripple.cloudshare.dto.request.SignUpRequest;
import com.ripple.cloudshare.dto.response.SignUpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    public AuthController(UserDAOService userDAOService) {
        this.userDAOService = userDAOService;
        this.logger = LoggerFactory.getLogger(CLASS_NAME);
    }

    @PostMapping("/sign-up")
    public SignUpResponse logRequest(@Valid @RequestBody SignUpRequest signUpRequest) {
        logger.info("Request received: " + signUpRequest);
        return userDAOService.createUser(signUpRequest);
    }

}
