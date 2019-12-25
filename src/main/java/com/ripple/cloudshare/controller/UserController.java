package com.ripple.cloudshare.controller;

import com.ripple.cloudshare.data.dao.UserDAOService;
import com.ripple.cloudshare.dto.RequestParamConverter;
import com.ripple.cloudshare.dto.entity.UserDTO;
import com.ripple.cloudshare.security.AuthUser;
import com.ripple.cloudshare.security.SecurityUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(UserController.USER_CONTROLLER_ROOT)
public class UserController {

    private static final String CLASS_NAME = "UserController";
    public static final String USER_CONTROLLER_ROOT = "users";

    private final Logger logger;
    private final UserDAOService userDAOService;

    public UserController(UserDAOService userDAOService) {
        this.userDAOService = userDAOService;
        this.logger = LoggerFactory.getLogger(CLASS_NAME);
    }

    @GetMapping("/{id}")
    public UserDTO getUser(@PathVariable(value = "id") String stringId){
        Long id = RequestParamConverter.longFromString(stringId, "Invalid Id");
        return UserDTO.fromUser(userDAOService.getById(id));
    }

    @GetMapping("/me")
    public UserDTO getCurrentUser(@AuthUser SecurityUser currentUser){
        return UserDTO.fromUser(userDAOService.getById(currentUser.getId()));
    }

}
