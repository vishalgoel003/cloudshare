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
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class VirtualMachineDAOService {

    public static final String TOP_LEVEL_DOMAIN = "cloud.ripple.com";
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
                .orElseThrow(() -> new RippleUserRuntimeException("No live machine found with given id"));
    }

    public VirtualMachineDetail getLiveVirtualMachineForUserById(Long virtualMachineId, Long userId) {
        return virtualMachineRepository.findLiveOneByIdAndUserId(virtualMachineId, userId)
                .map(VirtualMachineDetail::fromVirtualMachine)
                .map(vm -> {
                    vm.generateUrl(TOP_LEVEL_DOMAIN);
                    return vm;
                })
                .orElseThrow(() -> new RippleUserRuntimeException("No live machine found with given id for the user"));
    }

    public VirtualMachineDetail acquireVirtualMachineFromCloudManager(Long userId, VirtualMachineRequest virtualMachineRequest) {
        if(!cloudManager.checkAvailability(virtualMachineRequest)){
            throw new RippleAppRuntimeException("All servers are tightly occupied, please try later");
        }
        User userStub = new User();
        userStub.setId(userId);
        VirtualMachineDetail vmDetail = cloudManager.acquireVirtualMachine(userStub, virtualMachineRequest);
        if (vmDetail == null) {
            throw new RippleAppRuntimeException("Could not serve request, please try later");
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
                throw new RippleAppRuntimeException("Could not remove requested VM to delete");
            }
        } else {
            throw new RippleUserRuntimeException("Could not find requested VM to delete");
        }
    }

    public VirtualMachineDetail forceRemoveVirtualMachineForUser(Long virtualMachineId, Long userId) {
        logger.warn("forceRemoveVirtualMachineForUser called to remove virtualMachineId: " + virtualMachineId
            + ", userId: " + userId);
        return removeVirtualMachineByUser(virtualMachineId, userId);
    }
}
