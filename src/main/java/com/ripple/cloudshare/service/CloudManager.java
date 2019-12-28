package com.ripple.cloudshare.service;

import com.ripple.cloudshare.data.entity.User;

public interface CloudManager {

    public boolean checkAvailability(VirtualMachineRequest virtualMachineRequest);

    public VirtualMachineDetail acquireVirtualMachine(User user, VirtualMachineRequest virtualMachineRequest);

    public boolean forceRemoveVirtualMachine(Long virtualMachineId);

    public boolean removeVirtualMachineForUser(User user, Long virtualMachineId);

}
