package com.ripple.cloudshare.security;

import com.ripple.cloudshare.data.dao.UserDAOService;
import com.ripple.cloudshare.data.entity.User;
import com.ripple.cloudshare.data.entity.UserType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SecurityUserDetailsServiceTest {

    @Mock
    UserDAOService userDAOService;

    @InjectMocks
    SecurityUserDetailsService service;

    @Mock
    User databaseUser;

    @BeforeEach
    void setup() {
        when(databaseUser.getId()).thenReturn(1L);
        when(databaseUser.getEmail()).thenReturn("test@gmail.com");
        when(databaseUser.getPassword()).thenReturn("PassWord");
        when(databaseUser.getUserType()).thenReturn(UserType.NON_ADMIN);
        when(databaseUser.getDeleted()).thenReturn(false);
    }

    @Test
    void loadUserByUsername() {
        when(userDAOService.getByEmail("test@gmail.com")).thenReturn(databaseUser);

        UserDetails userDetails = service.loadUserByUsername("test@gmail.com");

        assertNotNull(userDetails);
        assertTrue(userDetails.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equalsIgnoreCase("ROLE_NON_ADMIN")));
        assertEquals(1, userDetails.getAuthorities().size());
    }

    @Test
    void loadUserById() {
        when(userDAOService.getById(1L)).thenReturn(databaseUser);

        UserDetails userDetails = service.loadUserById(1L);

        assertNotNull(userDetails);
        assertTrue(userDetails.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equalsIgnoreCase("ROLE_NON_ADMIN")));
        assertEquals(1, userDetails.getAuthorities().size());
    }
}