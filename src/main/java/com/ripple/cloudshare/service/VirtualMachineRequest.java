package com.ripple.cloudshare.service;

import com.ripple.cloudshare.data.entity.OperatingSystem;

public class VirtualMachineRequest {
    private Integer cpuCores;
    private Integer ram;
    private Integer hdd;
    private OperatingSystem operatingSystem;

    public VirtualMachineRequest(Integer cpuCores, Integer ram, Integer hdd, OperatingSystem operatingSystem) {
        this.cpuCores = cpuCores;
        this.ram = ram;
        this.hdd = hdd;
        this.operatingSystem = operatingSystem;
    }

    public Integer getCpuCores() {
        return cpuCores;
    }

    public void setCpuCores(Integer cpuCores) {
        this.cpuCores = cpuCores;
    }

    public Integer getRam() {
        return ram;
    }

    public void setRam(Integer ram) {
        this.ram = ram;
    }

    public Integer getHdd() {
        return hdd;
    }

    public void setHdd(Integer hdd) {
        this.hdd = hdd;
    }

    public OperatingSystem getOperatingSystem() {
        return operatingSystem;
    }

    public void setOperatingSystem(OperatingSystem operatingSystem) {
        this.operatingSystem = operatingSystem;
    }
}
