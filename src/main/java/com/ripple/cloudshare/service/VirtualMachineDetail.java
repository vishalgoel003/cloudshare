package com.ripple.cloudshare.service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ripple.cloudshare.data.entity.VirtualMachine;

import javax.validation.constraints.NotNull;

public class VirtualMachineDetail {

    private static final String HYPHEN = "-";
    private static final String SUB_DOMAIN_SEPARATOR = ".";
    
    @JsonProperty(value = "virtual_Machine_id")
    private Long virtualMachineId;
    @JsonIgnore
    private Long serverId;
    @JsonProperty(value = "user_id")
    private Long userId;
    @JsonProperty(value = "ssh_url")
    private String sshUrl;

    @JsonProperty(value = "virtual_Machine_params")
    private VirtualMachineRequest virtualMachineRequest;

    public VirtualMachineDetail() {}

    public VirtualMachineDetail(Long virtualMachineId, Long serverId, Long userId, VirtualMachineRequest virtualMachineRequest) {
        this.virtualMachineId = virtualMachineId;
        this.serverId = serverId;
        this.userId = userId;
        this.virtualMachineRequest = virtualMachineRequest;
    }

    public static VirtualMachineDetail fromVirtualMachine(VirtualMachine virtualMachine) {
        VirtualMachineRequest virtualMachineRequest = new VirtualMachineRequest(virtualMachine.getCpuCores(),
                virtualMachine.getRam(), virtualMachine.getHdd(), virtualMachine.getOperatingSystem());
        return new VirtualMachineDetail(virtualMachine.getId(), virtualMachine.getServer().getId(), virtualMachine.getUser().getId(),
                virtualMachineRequest);
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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public VirtualMachineRequest getVirtualMachineRequest() {
        return virtualMachineRequest;
    }

    public void setVirtualMachineRequest(VirtualMachineRequest virtualMachineRequest) {
        this.virtualMachineRequest = virtualMachineRequest;
    }

    public String getSshUrl() {
        return sshUrl;
    }

    public void setSshUrl(String sshUrl) {
        this.sshUrl = sshUrl;
    }

    public void generateUrl(@NotNull String topLevelDomain) {
        this.sshUrl = new StringBuilder("vm")
                .append(HYPHEN).append(virtualMachineId)
                .append(SUB_DOMAIN_SEPARATOR).append(topLevelDomain)
                .toString();
    }
}
