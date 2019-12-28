package com.ripple.cloudshare.service;

public class VirtualMachineDetail {

    Long serverId;
    Long virtualMachineId;

    public VirtualMachineDetail(Long serverId, Long virtualMachineId) {
        this.serverId = serverId;
        this.virtualMachineId = virtualMachineId;
    }

    public Long getServerId() {
        return serverId;
    }

    public void setServerId(Long serverId) {
        this.serverId = serverId;
    }

    public Long getVirtualMachineId() {
        return virtualMachineId;
    }

    public void setVirtualMachineId(Long virtualMachineId) {
        this.virtualMachineId = virtualMachineId;
    }
}
