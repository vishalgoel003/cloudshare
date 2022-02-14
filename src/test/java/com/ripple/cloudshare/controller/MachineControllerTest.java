package com.ripple.cloudshare.controller;

import com.ripple.cloudshare.data.dao.UserDAOService;
import com.ripple.cloudshare.data.dao.VirtualMachineDAOService;
import com.ripple.cloudshare.dto.response.MachineAcquireResponse;
import com.ripple.cloudshare.security.SecurityUser;
import com.ripple.cloudshare.service.VirtualMachineDetail;
import com.ripple.cloudshare.service.VirtualMachineRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static java.util.Collections.singletonList;
import static java.util.Optional.empty;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MachineControllerTest {

    @Mock
    VirtualMachineDAOService virtualMachineDAOService;
    @Mock
    UserDAOService userDAOService;

    @InjectMocks
    MachineController controller;

    @Mock
    SecurityUser mockSecurityUser;

    @Mock
    VirtualMachineDetail virtualMachineDetail;

    @Mock
    VirtualMachineRequest virtualMachineRequest;

    @BeforeEach
    void setup() {
        when(mockSecurityUser.getId()).thenReturn(1L);
    }

    @Test
    void getMyMachines() {
        when(virtualMachineDAOService.getLiveVirtualMachinesForUser(eq(1L))).thenReturn(singletonList(virtualMachineDetail));

        List<VirtualMachineDetail> machines = controller.getMyMachines(mockSecurityUser);

        assertNotNull(machines);
        assertEquals(1, machines.size());
        assertSame(virtualMachineDetail, machines.get(0));
    }

    @Test
    void getMyTopMachines() {
        when(virtualMachineDAOService.getLiveVirtualMachinesForUserOrderByRam(eq(1L), any(Integer.class)))
                .thenReturn(singletonList(virtualMachineDetail));

        List<VirtualMachineDetail> machines = controller.getMyTopMachines(mockSecurityUser, empty());

        assertNotNull(machines);
        assertEquals(1, machines.size());
        assertSame(virtualMachineDetail, machines.get(0));
    }

    @Test
    void getMachineDetails() {
        when(virtualMachineDAOService.getLiveVirtualMachineForUserById(eq(1L), eq(1L)))
                .thenReturn(virtualMachineDetail);

        VirtualMachineDetail vmDetail = controller.getMachineDetails(mockSecurityUser, "1");

        assertSame(virtualMachineDetail, vmDetail);
    }

    @Test
    void getGlobalMachines() {
        when(virtualMachineDAOService.getLiveVirtualMachines()).thenReturn(singletonList(virtualMachineDetail));

        List<VirtualMachineDetail> machines = controller.getGlobalMachines(mockSecurityUser);

        assertNotNull(machines);
        assertEquals(1, machines.size());
        assertSame(virtualMachineDetail, machines.get(0));
    }

    @Test
    void getGlobalTopMachines() {
        when(virtualMachineDAOService.getLiveVirtualMachinesOrderByRam(anyInt())).thenReturn(singletonList(virtualMachineDetail));

        List<VirtualMachineDetail> machines = controller.getGlobalTopMachines(mockSecurityUser, empty());

        assertNotNull(machines);
        assertEquals(1, machines.size());
        assertSame(virtualMachineDetail, machines.get(0));
    }

    @Test
    void getGlobalMachineDetails() {
        when(virtualMachineDAOService.getLiveVirtualMachineById(eq(1L))).thenReturn(virtualMachineDetail);

        VirtualMachineDetail vmDetails = controller.getGlobalMachineDetails(mockSecurityUser, "1");

        assertSame(virtualMachineDetail, vmDetails);
    }

    @Test
    void acquireVM() {
        when(virtualMachineDAOService.acquireVirtualMachineFromCloudManager(eq(1L), eq(virtualMachineRequest)))
                .thenReturn(virtualMachineDetail);
        when(virtualMachineDetail.getVirtualMachineId()).thenReturn(1L);
        when(virtualMachineDetail.getSshUrl()).thenReturn("Test URL");

        MachineAcquireResponse response = controller.acquireVM(virtualMachineRequest, mockSecurityUser);

        assertEquals(1L, response.getId());
        assertEquals("Test URL", response.getSshUrl());
    }

    @Test
    void removeMachine() {
        when(virtualMachineDAOService.getLiveVirtualMachineById(eq(1L))).thenReturn(virtualMachineDetail);
        when(virtualMachineDetail.getUserId()).thenReturn(1L);
        when(virtualMachineDetail.getVirtualMachineId()).thenReturn(1L);
        when(virtualMachineDAOService.removeVirtualMachineForUser(eq(1L), eq(1L))).thenReturn(virtualMachineDetail);

        VirtualMachineDetail removedMachine = controller.removeMachine(mockSecurityUser, "1");

        assertSame(virtualMachineDetail, removedMachine);
    }
}