package com.ripple.cloudshare.data.dao;

import com.ripple.cloudshare.data.entity.User;
import com.ripple.cloudshare.data.entity.UserType;
import com.ripple.cloudshare.data.repository.UserRepository;
import com.ripple.cloudshare.dto.request.SignUpRequest;
import com.ripple.cloudshare.exception.RippleUserRuntimeException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static com.ripple.cloudshare.ApplicationConstants.*;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalMatchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserDAOServiceTest {

    @InjectMocks
    UserDAOService userDAOService;

    @Mock
    UserRepository userRepository;

    @Mock
    SignUpRequest signUpRequest;

    @Mock
    User mockUser;

    @Mock
    User deletedMockUser;

    @Captor
    ArgumentCaptor<User> userCaptor;

    @Mock
    VirtualMachineDAOService virtualMachineDAOService;

    @Test
    void createUser() {
        when(signUpRequest.getName()).thenReturn("test user");
        when(signUpRequest.getEmail()).thenReturn("test.user@gmail.com");
        when(signUpRequest.getMobile()).thenReturn("1234512345");
        when(signUpRequest.getUserType()).thenReturn("NON_ADMIN");
        when(signUpRequest.getPassword()).thenReturn("test123");

        when(userRepository.findRecordsMatchingDetails(eq("test.user@gmail.com"), eq("1234512345"))).thenReturn(0l)
                .thenReturn(1l);

        userDAOService.createUser(signUpRequest);
        verify(userRepository).save(userCaptor.capture());
        User capturedUser = userCaptor.getValue();
        assertEquals("test user", capturedUser.getName());
        assertEquals("test.user@gmail.com", capturedUser.getEmail());
        assertEquals("1234512345", capturedUser.getMobile());
        assertEquals("NON_ADMIN", capturedUser.getUserType().toString());
        assertEquals("test123", capturedUser.getPassword());
        RippleUserRuntimeException ex = assertThrows(RippleUserRuntimeException.class, () -> {
            userDAOService.createUser(signUpRequest);
        });
        verify(userRepository, times(2)).findRecordsMatchingDetails(any(), any());
        assertEquals(EMAIL_OR_MOBILE_ALREADY_IN_USE, ex.getMessage());
    }

    @Test
    void getByEmail() {
        when(userRepository.findByEmail(eq("test.user@gmail.com"))).thenReturn(mockUser);
        when(userRepository.findByEmail(not(eq("test.user@gmail.com")))).thenReturn(null);

        User user = userDAOService.getByEmail("test.user@gmail.com");
        assertSame(mockUser, user);

        RippleUserRuntimeException ex = assertThrows(RippleUserRuntimeException.class, () -> {
            userDAOService.getByEmail("test.user2@gmail.com");
        });
        assertEquals(NO_USER_WITH_GIVEN_EMAIL, ex.getMessage());
    }

    @Test
    void getById() {
        when(userRepository.findById(eq(1l))).thenReturn(Optional.of(mockUser));
        when(mockUser.getDeleted()).thenReturn(false);
        when(userRepository.findById(not(eq(1l)))).thenReturn(Optional.empty());

        User user = userDAOService.getById(1l);
        assertSame(mockUser, user);

        RippleUserRuntimeException ex = assertThrows(RippleUserRuntimeException.class, () -> {
            userDAOService.getById(2l);
        });
        assertEquals(NO_USER_WITH_GIVEN_ID, ex.getMessage());
    }

    @Test
    void getAllUsers() {
        when(userRepository.findAll()).thenReturn(singletonList(mockUser));

        assertDoesNotThrow(() -> {
            List<User> users = userDAOService.getAllUsers();
            assertTrue(users.contains(mockUser));
            assertEquals(1, users.size());
        });
    }

    @Test
    void deleteById() {
        when(mockUser.getDeleted()).thenReturn(false);
        when(deletedMockUser.getDeleted()).thenReturn(true);
        when(mockUser.getUserType()).thenReturn(UserType.NON_ADMIN);
        when(userRepository.findById(eq(1l))).thenReturn(Optional.of(mockUser))
                .thenReturn(Optional.of(deletedMockUser));
        when(userRepository.save(eq(mockUser))).thenReturn(mockUser);

        User user = userDAOService.deleteById(1l);
        assertSame(mockUser, user);
        verify(virtualMachineDAOService, times(1)).deleteAllMachinesForUser(eq(mockUser));

        RippleUserRuntimeException ex = assertThrows(RippleUserRuntimeException.class, () -> {
            userDAOService.deleteById(1l);
            verify(deletedMockUser, times(1)).getDeleted();
        });

        assertEquals(NO_USER_WITH_GIVEN_ID, ex.getMessage());
        verify(userRepository, times(1)).save(any());
    }

    @Test
    void deleteByIdAdminUser() {
        when(userRepository.findById(eq(1l))).thenReturn(Optional.of(mockUser));
        when(mockUser.getDeleted()).thenReturn(false);
        when(mockUser.getUserType()).thenReturn(UserType.ADMIN);

        RippleUserRuntimeException ex = assertThrows(RippleUserRuntimeException.class, () -> {
            userDAOService.deleteById(1l);
        });
        assertEquals(ADMIN_CAN_NOT_DELETE_OTHER_ADMIN, ex.getMessage());
        verify(userRepository, never()).save(any());
        verify(virtualMachineDAOService, never()).deleteAllMachinesForUser(any());
    }
}