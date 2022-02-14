package com.ripple.cloudshare.controller;

import com.ripple.cloudshare.data.dao.UserDAOService;
import com.ripple.cloudshare.data.dao.VirtualMachineDAOService;
import com.ripple.cloudshare.data.entity.User;
import com.ripple.cloudshare.data.entity.UserType;
import com.ripple.cloudshare.dto.RequestParamConverter;
import com.ripple.cloudshare.dto.response.MachineAcquireResponse;
import com.ripple.cloudshare.exception.RippleUserRuntimeException;
import com.ripple.cloudshare.security.AuthUser;
import com.ripple.cloudshare.security.SecurityUser;
import com.ripple.cloudshare.service.VirtualMachineDetail;
import com.ripple.cloudshare.service.VirtualMachineRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(MachineController.MACHINE_CONTROLLER_ROOT)
public class MachineController {

    private static final String CLASS_NAME = "MachineController";
    public static final String MACHINE_CONTROLLER_ROOT = "machine";

    private final Logger logger;
    private final VirtualMachineDAOService virtualMachineDAOService;

    private final UserDAOService userDAOService;

    public MachineController(VirtualMachineDAOService virtualMachineDAOService,
                             UserDAOService userDAOService) {
        this.virtualMachineDAOService = virtualMachineDAOService;
        this.userDAOService = userDAOService;
        this.logger = LoggerFactory.getLogger(CLASS_NAME);
    }

    @GetMapping
    public List<VirtualMachineDetail> getMyMachines(@AuthUser SecurityUser currentUser){
        logger.info("getMyMachines called for user id: " + currentUser.getId());
        return virtualMachineDAOService.getLiveVirtualMachinesForUser(currentUser.getId());
    }

    @GetMapping("/top")
    public List<VirtualMachineDetail> getMyTopMachines(@AuthUser SecurityUser currentUser,
                                                       @RequestParam Optional<Integer> limit){
        logger.info("getMyTopMachines called for user id: " + currentUser.getId() + ", limit: "
                + limit.map(Object::toString).orElse("not provided, using default"));
        return virtualMachineDAOService
                .getLiveVirtualMachinesForUserOrderByRam(currentUser.getId(), limit.orElse(5));
    }

    @GetMapping("/{id}")
    public VirtualMachineDetail getMachineDetails(@AuthUser SecurityUser currentUser,
                                                  @PathVariable(value = "id") String stringId){
        Long machineId = RequestParamConverter.longFromString(stringId, "Invalid Id");
        logger.info("getMachineDetails called by user id: " + currentUser.getId()
                + " for machine_id: " + stringId);
        return virtualMachineDAOService.getLiveVirtualMachineForUserById(machineId, currentUser.getId());
    }

    @GetMapping("/global")
    @PreAuthorize("hasRole('ADMIN')")
    public List<VirtualMachineDetail> getGlobalMachines(@AuthUser SecurityUser currentUser){
        logger.info("getGlobalMachines called for user id: " + currentUser.getId());
        return virtualMachineDAOService.getLiveVirtualMachines();
    }

    @GetMapping("/global/top")
    @PreAuthorize("hasRole('ADMIN')")
    public List<VirtualMachineDetail> getGlobalTopMachines(@AuthUser SecurityUser currentUser,
                                                           @RequestParam Optional<Integer> limit){
        logger.info("getGlobalTopMachines called for user id: " + currentUser.getId() + ", limit: "
                + (limit.isPresent() ? limit.get() : "absent"));
        return virtualMachineDAOService.getLiveVirtualMachinesOrderByRam(limit.orElse(10));
    }

    @GetMapping("/global/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public VirtualMachineDetail getGlobalMachineDetails(@AuthUser SecurityUser currentUser,
                                                  @PathVariable(value = "id") String stringId){
        Long machineId = RequestParamConverter.longFromString(stringId, "Invalid Id");
        logger.info("getGlobalMachineDetails called by user id: " + currentUser.getId()
                + " for machine_id: " + stringId);
        return virtualMachineDAOService.getLiveVirtualMachineById(machineId);
    }

    @PostMapping("/acquire")
    public MachineAcquireResponse acquireVM(@Valid @RequestBody VirtualMachineRequest virtualMachineRequest,
                                            @AuthUser SecurityUser currentUser) {
        logger.info("User id " + currentUser.getId() + " requested VM: " + virtualMachineRequest);
        VirtualMachineDetail virtualMachineDetail = virtualMachineDAOService
                .acquireVirtualMachineFromCloudManager(currentUser.getId(), virtualMachineRequest);
        return MachineAcquireResponse.fromVirtualMachineDetail(virtualMachineDetail);
    }

    @DeleteMapping("/{id}")
    public VirtualMachineDetail removeMachine(@AuthUser SecurityUser currentUser,
                                              @PathVariable(value = "id") String stringId) {
        Long machineId = RequestParamConverter.longFromString(stringId, "Invalid Id");
        VirtualMachineDetail machine = virtualMachineDAOService.getLiveVirtualMachineById(machineId);
        if (machine.getUserId() != currentUser.getId()){
            return adminRemoveMachine(machine, currentUser);
        }
        logger.warn("removeMachine called by user id:" + currentUser.getId()
                + " to remove virtualMachineId: " + machine.getVirtualMachineId());
        return virtualMachineDAOService.removeVirtualMachineForUser(machineId, currentUser.getId());
    }

    @PreAuthorize("hasRole('ADMIN')")
    private VirtualMachineDetail adminRemoveMachine(VirtualMachineDetail machine, SecurityUser currentUser) {
        User user = userDAOService.getById(machine.getUserId());
        if (user.getUserType().equals(UserType.ADMIN)) {
            throw new RippleUserRuntimeException("Can not force remove machine from another admin");
        }
        logger.warn("removeMachine called by user id:" + currentUser.getId()
                + " to remove virtualMachineId: " + machine.getVirtualMachineId()
                + ", belonging to user id: " + machine.getUserId());
        return virtualMachineDAOService
                .forceRemoveVirtualMachineForUser(machine.getVirtualMachineId(), machine.getUserId());
    }

}
