package com.ripple.cloudshare.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ripple.cloudshare.service.VirtualMachineDetail;

public class MachineAcquireResponse {
    private Long id;
    private String path;
    @JsonProperty(value = "ssh_url")
    private String sshUrl;

    public static MachineAcquireResponse fromVirtualMachineDetail(VirtualMachineDetail virtualMachineDetail){
        return new MachineAcquireResponse(virtualMachineDetail.getVirtualMachineId(),
                "/machine/" + virtualMachineDetail.getVirtualMachineId(),
                virtualMachineDetail.getSshUrl());
    }

    public MachineAcquireResponse(Long id, String path, String sshUrl) {
        this.id = id;
        this.path = path;
        this.sshUrl = sshUrl;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getSshUrl() {
        return sshUrl;
    }

    public void setSshUrl(String sshUrl) {
        this.sshUrl = sshUrl;
    }
}
