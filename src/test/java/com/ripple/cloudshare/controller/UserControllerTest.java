package com.ripple.cloudshare.controller;

import com.ripple.cloudshare.data.dao.UserDAOService;
import com.ripple.cloudshare.data.entity.User;
import com.ripple.cloudshare.dto.entity.UserDTO;
import com.ripple.cloudshare.exception.RippleAppRuntimeException;
import com.ripple.cloudshare.exception.RippleUserRuntimeException;
import com.ripple.cloudshare.security.SecurityUser;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

import java.util.List;
import java.util.Random;

import static com.ripple.cloudshare.ApplicationConstants.*;
import static org.junit.jupiter.api.Assertions.*;

@Disabled
@SpringBootTest
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
class UserControllerTest {

    @Autowired
    private UserController userController;

    @Autowired
    private UserDAOService userDAOService;

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void getUserWithAuth() {
        UserDTO dto = userController.getUser("1");
        Assertions.assertNotNull(dto);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void getUserWithAuthInvalidUserId() {
        Integer fakeUserId = (new Random()).nextInt(1000) + 1000;

        RippleUserRuntimeException ex = assertThrows(RippleUserRuntimeException.class, () -> {
            userController.getUser(fakeUserId.toString());
        });
        assertEquals(NO_USER_WITH_GIVEN_ID, ex.getMessage());
    }

    @Test
    void getUserWithoutAuth() {
        Assertions.assertThrows(Exception.class, () -> {
            userController.getUser("1");
        });
    }

    @Test
    void getCurrentUserWithValidUser() {
        //Valid security user
        User user = userDAOService.getByEmail("no-reply@ripple.com");
        SecurityUser securityUser = SecurityUser.create(user);

        UserDTO dto = userController.getCurrentUser(securityUser);
        assertNotNull(dto);
    }

    @Test
    @WithMockUser(roles = {"NON_ADMIN"})
    void getAllUsersWithoutAdminRole() {
        assertThrows(Exception.class, () -> {
            userController.getAllUsers();
        });
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void getAllUsersWithAdminRole() {
        List<UserDTO> users = userController.getAllUsers();

        assertNotNull(users);
        assertNotEquals(0, users.size());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void deleteUserOtherNonAdmin() {
        //Valid security user with admin
        User user = userDAOService.getByEmail("no-reply@ripple.com");
        SecurityUser securityUser = SecurityUser.create(user);

        User normalUser = userDAOService.getByEmail("vishalgoel5@gmail.com");

        assertDoesNotThrow(() -> {
            userController.deleteUser(normalUser.getId().toString(), securityUser);
        });
        User deletedUser = userDAOService.getByEmail("vishalgoel5@gmail.com");
        assertEquals(true, deletedUser.getDeleted());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void deleteUserSelf() {
        //Valid security user with admin
        User user = userDAOService.getByEmail("no-reply@ripple.com");
        SecurityUser securityUser = SecurityUser.create(user);

        RippleAppRuntimeException ex = assertThrows(RippleAppRuntimeException.class, () -> {
            userController.deleteUser(user.getId().toString(), securityUser);
        });

        assertEquals(SELF_DESTRUCT_NOT_ALLOWED, ex.getMessage());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void deleteUserOtherAdmin() {
        //Valid security user with admin
        User user = userDAOService.getByEmail("no-reply@ripple.com");
        SecurityUser securityUser = SecurityUser.create(user);

        User otherAdminUser = userDAOService.getByEmail("vishalgoel03@gmail.com");

        RippleUserRuntimeException ex = assertThrows(RippleUserRuntimeException.class, () -> {
            userController.deleteUser(otherAdminUser.getId().toString(), securityUser);
        });

        assertEquals(ADMIN_CAN_NOT_DELETE_OTHER_ADMIN, ex.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, ex.getResponseStatus());
    }
}