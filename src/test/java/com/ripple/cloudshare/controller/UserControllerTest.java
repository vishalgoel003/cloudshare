package com.ripple.cloudshare.controller;

import com.ripple.cloudshare.data.dao.UserDAOService;
import com.ripple.cloudshare.data.entity.User;
import com.ripple.cloudshare.data.entity.UserType;
import com.ripple.cloudshare.dto.entity.UserDTO;
import com.ripple.cloudshare.exception.RippleAppRuntimeException;
import com.ripple.cloudshare.exception.RippleUserRuntimeException;
import com.ripple.cloudshare.security.SecurityUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static com.ripple.cloudshare.ApplicationConstants.SELF_DESTRUCT_NOT_ALLOWED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    public static final String TEST_USER = "Test User";
    public static final String TEST_EMAIL = "test@gmail.com";
    public static final String TEST_MOBILE = "9876543219";

    @Mock
    UserDAOService userDAOService;

    @InjectMocks
    UserController controller;

    @Mock
    User mockUser;

    @Mock
    SecurityUser mockSecurityUser;

    @BeforeEach
    void setup() {
        when(mockUser.getId()).thenReturn(1l);
        when(mockUser.getName()).thenReturn(TEST_USER);
        when(mockUser.getEmail()).thenReturn(TEST_EMAIL);
        when(mockUser.getMobile()).thenReturn(TEST_MOBILE);
        when(mockUser.getUserType()).thenReturn(UserType.NON_ADMIN);
    }

    @Test
    void getUser() {
        when(userDAOService.getById(eq(1l))).thenReturn(mockUser);

        UserDTO userDTO = controller.getUser("1");

        assertEquals(1l, userDTO.getId());
        assertEquals(TEST_USER, userDTO.getName());
        assertEquals(TEST_EMAIL, userDTO.getEmail());
        assertEquals(TEST_MOBILE, userDTO.getMobile());
        assertEquals(UserType.NON_ADMIN.name(), userDTO.getUserType());

        RippleUserRuntimeException ex = assertThrows(RippleUserRuntimeException.class, () -> {
            controller.getUser("abc");
        });
        assertEquals("Invalid Id", ex.getMessage());
    }

    @Test
    void getCurrentUser() {
        when(userDAOService.getById(eq(1l))).thenReturn(mockUser);
        when(mockSecurityUser.getId()).thenReturn(1l);

        UserDTO userDTO = controller.getCurrentUser(mockSecurityUser);

        assertEquals(1l, userDTO.getId());
        assertEquals(TEST_USER, userDTO.getName());
        assertEquals(TEST_EMAIL, userDTO.getEmail());
        assertEquals(TEST_MOBILE, userDTO.getMobile());
        assertEquals(UserType.NON_ADMIN.name(), userDTO.getUserType());
    }

    @Test
    void getAllUsers() {
        when(mockUser.getId()).thenReturn(1l).thenReturn(2l);
        when(userDAOService.getAllUsers()).thenReturn(Arrays.asList(mockUser, mockUser));

        List<UserDTO> users = controller.getAllUsers();

        assertEquals(2, users.size());
        assertTrue(users.stream().anyMatch(user -> user.getId() == 1l));
        assertTrue(users.stream().anyMatch(user -> user.getId() == 2l));
    }

    @Test
    void deleteUser() {
        when(userDAOService.deleteById(eq(1l))).thenReturn(mockUser);
        when(mockSecurityUser.getId()).thenReturn(2l);

        UserDTO userDTO = controller.deleteUser("1", mockSecurityUser);

        assertEquals(1l, userDTO.getId());
        assertEquals(TEST_USER, userDTO.getName());
        assertEquals(TEST_EMAIL, userDTO.getEmail());
        assertEquals(TEST_MOBILE, userDTO.getMobile());
        assertEquals(UserType.NON_ADMIN.name(), userDTO.getUserType());

        when(mockSecurityUser.getId()).thenReturn(1l);

        RippleAppRuntimeException ex = assertThrows(RippleAppRuntimeException.class, () -> {
            controller.deleteUser("1", mockSecurityUser);
        });
        assertEquals(SELF_DESTRUCT_NOT_ALLOWED, ex.getMessage());
    }
}