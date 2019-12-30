package com.ripple.cloudshare.service;

import com.ripple.cloudshare.data.entity.Server;
import com.ripple.cloudshare.data.entity.User;

import java.util.List;

public interface CloudManager {

    //Enquiry
    public boolean checkAvailability(VirtualMachineRequest virtualMachineRequest);

    //Acquisition of VM
    public VirtualMachineDetail acquireVirtualMachine(User user, VirtualMachineRequest virtualMachineRequest);

    //Removal of VM
    public boolean removeVirtualMachine(Long virtualMachineId);

    //Addition of Server
    public boolean registerServer(Server server);

    //Removal request for server
    public boolean deregisterServer(Server server);

}
