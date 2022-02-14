package com.ripple.cloudshare.controller;

import com.ripple.cloudshare.data.dao.UserDAOService;
import com.ripple.cloudshare.dto.RequestParamConverter;
import com.ripple.cloudshare.dto.entity.UserDTO;
import com.ripple.cloudshare.exception.RippleAppRuntimeException;
import com.ripple.cloudshare.security.AuthUser;
import com.ripple.cloudshare.security.SecurityUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static com.ripple.cloudshare.ApplicationConstants.SELF_DESTRUCT_NOT_ALLOWED;

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
    @PreAuthorize("hasRole('ADMIN')")
    public UserDTO getUser(@PathVariable(value = "id") String stringId){
        Long id = RequestParamConverter.longFromString(stringId, "Invalid Id");
        return UserDTO.fromUser(userDAOService.getById(id));
    }

    @GetMapping("/me")
    public UserDTO getCurrentUser(@AuthUser SecurityUser currentUser){
        logger.info("getCurrentUser called for user id: " + currentUser.getId());
        return UserDTO.fromUser(userDAOService.getById(currentUser.getId()));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserDTO> getAllUsers(){
        return userDAOService.getAllUsers().stream()
                .map(UserDTO::fromUser)
                .collect(Collectors.toList());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public UserDTO deleteUser(@PathVariable(value = "id") String stringId, @AuthUser SecurityUser currentUser){
        Long id = RequestParamConverter.longFromString(stringId, "Invalid Id");
        if (currentUser.getId().equals(id)){
            logger.warn("User tried deleting self: " + currentUser.getId());
            throw new RippleAppRuntimeException(SELF_DESTRUCT_NOT_ALLOWED);
        }
        return UserDTO.fromUser(userDAOService.deleteById(id));
    }

}
