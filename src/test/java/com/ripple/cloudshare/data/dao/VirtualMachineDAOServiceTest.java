package com.ripple.cloudshare.data.dao;

import com.ripple.cloudshare.data.entity.OperatingSystem;
import com.ripple.cloudshare.data.entity.Server;
import com.ripple.cloudshare.data.entity.User;
import com.ripple.cloudshare.data.entity.VirtualMachine;
import com.ripple.cloudshare.data.repository.VirtualMachineRepository;
import com.ripple.cloudshare.exception.RippleAppRuntimeException;
import com.ripple.cloudshare.exception.RippleUserRuntimeException;
import com.ripple.cloudshare.service.CloudManager;
import com.ripple.cloudshare.service.VirtualMachineDetail;
import com.ripple.cloudshare.service.VirtualMachineRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static com.ripple.cloudshare.ApplicationConstants.*;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VirtualMachineDAOServiceTest {

    @InjectMocks
    VirtualMachineDAOService service;

    @Mock
    CloudManager cloudManager;

    @Mock
    VirtualMachineRepository virtualMachineRepository;

    @Mock
    VirtualMachine virtualMachine1;

    @Mock
    VirtualMachine virtualMachine2;

    @Mock
    Server server;

    @Mock
    User user;

    @Mock
    VirtualMachineRequest mockVirtualMachineRequest;

    @Captor
    ArgumentCaptor<User> userCaptor;

    @Captor
    ArgumentCaptor<Long> idCaptor;

    @Mock
    VirtualMachineDetail mockVirtualMachineDetail;

    void initMockVM1() {
        when(virtualMachine1.getCpuCores()).thenReturn(2);
        when(virtualMachine1.getRam()).thenReturn(4);
        when(virtualMachine1.getHdd()).thenReturn(16);
        when(virtualMachine1.getOperatingSystem()).thenReturn(OperatingSystem.LINUX);
        when(virtualMachine1.getId()).thenReturn(3L);

        when(virtualMachine1.getServer()).thenReturn(server);
        when(server.getId()).thenReturn(1L);

        when(virtualMachine1.getUser()).thenReturn(user);
        when(user.getId()).thenReturn(2L);
    }

    void initMockVM2() {
        when(virtualMachine2.getCpuCores()).thenReturn(4);
        when(virtualMachine2.getRam()).thenReturn(8);
        when(virtualMachine2.getHdd()).thenReturn(16);
        when(virtualMachine2.getOperatingSystem()).thenReturn(OperatingSystem.WINDOWS);
        when(virtualMachine2.getId()).thenReturn(4L);

        when(virtualMachine2.getServer()).thenReturn(server);
        when(server.getId()).thenReturn(1L);

        when(virtualMachine2.getUser()).thenReturn(user);
        when(user.getId()).thenReturn(2L);
    }

    @Test
    void getLiveVirtualMachines() {
        initMockVM1();
        initMockVM2();
        when(virtualMachineRepository.findAll()).thenReturn(asList(virtualMachine1, virtualMachine2));

        List<VirtualMachineDetail> vms = service.getLiveVirtualMachines();

        assertEquals(3L, vms.get(0).getVirtualMachineId());
        assertEquals(4L, vms.get(1).getVirtualMachineId());
        assertNotNull(vms.get(0).getSshUrl());
        assertNotNull(vms.get(1).getSshUrl());
    }

    @Test
    void getLiveVirtualMachinesOrderByRam() {
        initMockVM2();
        when(virtualMachineRepository.findAllLiveOrderByRamWithPaging(any())).thenReturn(asList(virtualMachine2));

        List<VirtualMachineDetail> vms = service.getLiveVirtualMachinesOrderByRam(1);

        assertEquals(4, vms.get(0).getVirtualMachineRequest().getCpuCores());
        assertEquals(8, vms.get(0).getVirtualMachineRequest().getRam());
        assertEquals(16, vms.get(0).getVirtualMachineRequest().getHdd());
        assertEquals(OperatingSystem.WINDOWS, vms.get(0).getVirtualMachineRequest().getOperatingSystem());
        assertEquals(4L, vms.get(0).getVirtualMachineId());
        assertEquals(1L, vms.get(0).getServerId());
        assertEquals(2L, vms.get(0).getUserId());
        assertEquals("vm-4.cloud.ripple.com", vms.get(0).getSshUrl());
    }

    @Test
    void getLiveVirtualMachinesForUser() {
        initMockVM1();
        initMockVM2();
        when(virtualMachineRepository.findAllLiveByUserId(eq(2L))).thenReturn(asList(virtualMachine2, virtualMachine1));

        List<VirtualMachineDetail> vms = service.getLiveVirtualMachinesForUser(2L);

        assertTrue(vms.stream().anyMatch(vm -> vm.getVirtualMachineId() == 4L));
        assertTrue(vms.stream().anyMatch(vm -> vm.getVirtualMachineId() == 3L));
        assertEquals(2, vms.size());
    }

    @Test
    void getLiveVirtualMachinesForUserOrderByRam() {
        initMockVM2();
        when(virtualMachineRepository.findAllLiveByUserIdOrderByRamWithPaging(eq(2L), any()))
                .thenReturn(singletonList(virtualMachine2));

        List<VirtualMachineDetail> vms = service.getLiveVirtualMachinesForUserOrderByRam(2L, 1);

        assertEquals(1, vms.size());
        assertEquals(4L, vms.get(0).getVirtualMachineId());
    }

    @Test
    void getLiveVirtualMachineById() {
        initMockVM1();
        when(virtualMachineRepository.findLiveOneById(eq(2L)))
                .thenReturn(Optional.of(virtualMachine1));

        VirtualMachineDetail vm = service.getLiveVirtualMachineById(2L);

        assertEquals(2, vm.getVirtualMachineRequest().getCpuCores());
        assertEquals(4, vm.getVirtualMachineRequest().getRam());
        assertEquals(16, vm.getVirtualMachineRequest().getHdd());
        assertEquals(OperatingSystem.LINUX, vm.getVirtualMachineRequest().getOperatingSystem());
        assertEquals(3L, vm.getVirtualMachineId());
        assertEquals(1L, vm.getServerId());
        assertEquals(2L, vm.getUserId());
        assertEquals("vm-3.cloud.ripple.com", vm.getSshUrl());
    }

    @Test
    void getLiveVirtualMachineForUserById() {
        initMockVM1();
        when(virtualMachineRepository.findLiveOneByIdAndUserId(anyLong(), anyLong()))
                .thenReturn(Optional.empty());
        when(virtualMachineRepository.findLiveOneByIdAndUserId(eq(2L), eq(2L)))
                .thenReturn(Optional.of(virtualMachine1));

        VirtualMachineDetail vm = service.getLiveVirtualMachineForUserById(2L, 2L);

        assertEquals(2, vm.getVirtualMachineRequest().getCpuCores());
        assertEquals(4, vm.getVirtualMachineRequest().getRam());
        assertEquals(16, vm.getVirtualMachineRequest().getHdd());
        assertEquals(OperatingSystem.LINUX, vm.getVirtualMachineRequest().getOperatingSystem());
        assertEquals(3L, vm.getVirtualMachineId());
        assertEquals(1L, vm.getServerId());
        assertEquals(2L, vm.getUserId());
        assertEquals("vm-3.cloud.ripple.com", vm.getSshUrl());

        RippleUserRuntimeException ex = assertThrows(RippleUserRuntimeException.class, () -> {
            service.getLiveVirtualMachineForUserById(1L, 2L);
        });
        assertEquals(NO_MACHINE_WITH_ID_FOR_USER, ex.getMessage());
    }

    @Test
    void acquireVirtualMachineFromCloudManager() {
        when(cloudManager.checkAvailability(eq(mockVirtualMachineRequest))).thenReturn(false);

        RippleAppRuntimeException ex = assertThrows(RippleAppRuntimeException.class, () -> {
            service.acquireVirtualMachineFromCloudManager(2L, mockVirtualMachineRequest);
        });
        assertEquals(NO_CAPACITY_AVAILABLE, ex.getMessage());

        when(cloudManager.checkAvailability(eq(mockVirtualMachineRequest))).thenReturn(true);
        when(cloudManager.acquireVirtualMachine(userCaptor.capture(), eq(mockVirtualMachineRequest)))
                .thenReturn(null);

        assertThrows(RippleAppRuntimeException.class, () -> {
            service.acquireVirtualMachineFromCloudManager(2L, mockVirtualMachineRequest);
        });
        assertEquals(NO_CAPACITY_AVAILABLE, ex.getMessage());
        User capturedUser = userCaptor.getValue();
        assertEquals(2L, capturedUser.getId());

        when(cloudManager.checkAvailability(eq(mockVirtualMachineRequest))).thenReturn(true);
        when(cloudManager.acquireVirtualMachine(userCaptor.capture(), eq(mockVirtualMachineRequest)))
                .thenReturn(mockVirtualMachineDetail);

        VirtualMachineDetail vmDetail = service.acquireVirtualMachineFromCloudManager(2L, mockVirtualMachineRequest);
        assertSame(mockVirtualMachineDetail, vmDetail);
        verify(mockVirtualMachineDetail).generateUrl(eq(TOP_LEVEL_DOMAIN));
    }

    @Test
    void removeVirtualMachineForUser() {
        removeVMForUserAssertions();
    }

    private void removeVMForUserAssertions() {
        initMockVM1();
        when(virtualMachineRepository.findLiveOneByIdAndUserId(eq(3L), eq(2L)))
                .thenReturn(Optional.of(virtualMachine1));
        when(cloudManager.removeVirtualMachine(eq(3L))).thenReturn(true);

        VirtualMachineDetail vmDetails = service.removeVirtualMachineForUser(3L, 2L);

        assertEquals(2, vmDetails.getVirtualMachineRequest().getCpuCores());
        assertEquals(4, vmDetails.getVirtualMachineRequest().getRam());
        assertEquals(16, vmDetails.getVirtualMachineRequest().getHdd());
        assertEquals(OperatingSystem.LINUX, vmDetails.getVirtualMachineRequest().getOperatingSystem());
        assertEquals(3L, vmDetails.getVirtualMachineId());
        assertEquals(1L, vmDetails.getServerId());
        assertEquals(2L, vmDetails.getUserId());
        assertEquals("vm-3.cloud.ripple.com", vmDetails.getSshUrl());

        when(cloudManager.removeVirtualMachine(eq(3L))).thenReturn(false);

        RippleAppRuntimeException ex = assertThrows(RippleAppRuntimeException.class, () -> {
            service.removeVirtualMachineForUser(3L, 2L);
        });
        assertEquals(COULD_NOT_REMOVE_VM, ex.getMessage());
    }

    @Test
    void forceRemoveVirtualMachineForUser() {
        removeVMForUserAssertions();
    }

    @Test
    void deleteAllMachinesForUser() {
        initMockVM1();
        initMockVM2();
        when(virtualMachineRepository.findAllLiveByUserId(eq(2L))).thenReturn(asList(virtualMachine2, virtualMachine1));
        when(virtualMachineRepository.findLiveOneByIdAndUserId(eq(3L), eq(2L)))
                .thenReturn(Optional.of(virtualMachine1));
        when(cloudManager.removeVirtualMachine(eq(3L))).thenReturn(true);
        when(virtualMachineRepository.findLiveOneByIdAndUserId(eq(4L), eq(2L)))
                .thenReturn(Optional.of(virtualMachine2));
        when(cloudManager.removeVirtualMachine(eq(4L))).thenReturn(true);
        when(user.getId()).thenReturn(2L);

        service.deleteAllMachinesForUser(user);

        verify(cloudManager,times(2)).removeVirtualMachine(idCaptor.capture());
        List<Long> deletedVMIds = idCaptor.getAllValues();
        assertEquals(2, deletedVMIds.size());
        assertTrue(deletedVMIds.contains(3L));
        assertTrue(deletedVMIds.contains(4L));
    }
}