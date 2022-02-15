package com.ripple.cloudshare.data.dao;

import com.ripple.cloudshare.data.entity.User;
import com.ripple.cloudshare.data.repository.VirtualMachineRepository;
import com.ripple.cloudshare.exception.RippleAppRuntimeException;
import com.ripple.cloudshare.exception.RippleUserRuntimeException;
import com.ripple.cloudshare.service.CloudManager;
import com.ripple.cloudshare.service.VirtualMachineDetail;
import com.ripple.cloudshare.service.VirtualMachineRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.ripple.cloudshare.ApplicationConstants.*;

@Service
public class VirtualMachineDAOService {

    private static final String CLASS_NAME = "VirtualMachineDAOService";

    private static final Logger logger = LoggerFactory.getLogger(CLASS_NAME);

    private final CloudManager cloudManager;

    //read-only access
    private final VirtualMachineRepository virtualMachineRepository;

    public VirtualMachineDAOService(CloudManager cloudManager, VirtualMachineRepository virtualMachineRepository) {
        this.cloudManager = cloudManager;
        this.virtualMachineRepository = virtualMachineRepository;
    }

    public List<VirtualMachineDetail> getLiveVirtualMachines() {
        return virtualMachineRepository.findAll().stream()
                .map(VirtualMachineDetail::fromVirtualMachine)
                .peek(vm -> vm.generateUrl(TOP_LEVEL_DOMAIN))
                .collect(Collectors.toList());
    }

    public List<VirtualMachineDetail> getLiveVirtualMachinesOrderByRam(int limit) {
        return virtualMachineRepository.findAllLiveOrderByRamWithPaging(PageRequest.of(0, limit)).stream()
                .map(VirtualMachineDetail::fromVirtualMachine)
                .peek(vm -> vm.generateUrl(TOP_LEVEL_DOMAIN))
                .collect(Collectors.toList());
    }

    public List<VirtualMachineDetail> getLiveVirtualMachinesForUser(Long userId) {
        return virtualMachineRepository.findAllLiveByUserId(userId).stream()
                .map(VirtualMachineDetail::fromVirtualMachine)
                .peek(vm -> vm.generateUrl(TOP_LEVEL_DOMAIN))
                .collect(Collectors.toList());
    }

    public List<VirtualMachineDetail> getLiveVirtualMachinesForUserOrderByRam(Long userId, int limit) {
        return virtualMachineRepository.findAllLiveByUserIdOrderByRamWithPaging(userId, PageRequest.of(0, limit))
                .stream()
                    .map(VirtualMachineDetail::fromVirtualMachine)
                    .peek(vm -> vm.generateUrl(TOP_LEVEL_DOMAIN))
                    .collect(Collectors.toList());
    }

    public VirtualMachineDetail getLiveVirtualMachineById(Long virtualMachineId) {
        return virtualMachineRepository.findLiveOneById(virtualMachineId)
                .map(VirtualMachineDetail::fromVirtualMachine)
                .map(vm -> {
                    vm.generateUrl(TOP_LEVEL_DOMAIN);
                    return vm;
                })
                .orElseThrow(() -> new RippleUserRuntimeException(NO_MACHINE_WITH_ID));
    }

    public VirtualMachineDetail getLiveVirtualMachineForUserById(Long virtualMachineId, Long userId) {
        return virtualMachineRepository.findLiveOneByIdAndUserId(virtualMachineId, userId)
                .map(VirtualMachineDetail::fromVirtualMachine)
                .map(vm -> {
                    vm.generateUrl(TOP_LEVEL_DOMAIN);
                    return vm;
                })
                .orElseThrow(() -> new RippleUserRuntimeException(NO_MACHINE_WITH_ID_FOR_USER));
    }

    public VirtualMachineDetail acquireVirtualMachineFromCloudManager(Long userId, VirtualMachineRequest virtualMachineRequest) {
        if(!cloudManager.checkAvailability(virtualMachineRequest)){
            throw new RippleAppRuntimeException(NO_CAPACITY_AVAILABLE);
        }
        User userStub = new User();
        userStub.setId(userId);
        VirtualMachineDetail vmDetail = cloudManager.acquireVirtualMachine(userStub, virtualMachineRequest);
        if (vmDetail == null) {
            throw new RippleAppRuntimeException(NO_CAPACITY_AVAILABLE);
        }
        vmDetail.generateUrl(TOP_LEVEL_DOMAIN);
        return vmDetail;
    }

    public VirtualMachineDetail removeVirtualMachineForUser(Long machineId, Long userId) {
        return removeVirtualMachineByUser(machineId, userId);
    }

    private VirtualMachineDetail removeVirtualMachineByUser(Long machineId, Long userId) {
        VirtualMachineDetail virtualMachineDetail = getLiveVirtualMachineForUserById(machineId, userId);
        if (virtualMachineDetail != null) {
            boolean success = cloudManager.removeVirtualMachine(machineId);
            if (success) {
                return virtualMachineDetail;
            } else {
                throw new RippleAppRuntimeException(COULD_NOT_REMOVE_VM);
            }
        } else {
            throw new RippleUserRuntimeException(COULD_NOT_REMOVE_VM);
        }
    }

    public VirtualMachineDetail forceRemoveVirtualMachineForUser(Long virtualMachineId, Long userId) {
        logger.warn("forceRemoveVirtualMachineForUser called to remove virtualMachineId: " + virtualMachineId
            + ", userId: " + userId);
        return removeVirtualMachineByUser(virtualMachineId, userId);
    }

    @Async
    public void deleteAllMachinesForUser(User user) {
        logger.info("deleting VMs in async mode for user id: " + user.getId());
        List<VirtualMachineDetail> machines = getLiveVirtualMachinesForUser(user.getId());
        for (VirtualMachineDetail machine : machines) {
            removeVirtualMachineForUser(machine.getVirtualMachineId(), user.getId());
        }
    }
}
