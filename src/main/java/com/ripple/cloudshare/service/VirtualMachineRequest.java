package com.ripple.cloudshare.service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ripple.cloudshare.data.entity.OperatingSystem;
import com.ripple.cloudshare.data.entity.VirtualMachine;
import com.ripple.cloudshare.dto.validator.ValueOfEnum;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import static com.ripple.cloudshare.ApplicationConstants.INVALID_OPERATING_SYSTEM_MESSAGE;

public class VirtualMachineRequest {

    @JsonProperty(value = "cpu_cores")
    @Min(value = 1)
    @Max(value = 32)
    private Integer cpuCores;

    @JsonProperty(value = "ram")
    @Min(value = 1)
    @Max(value = 128)
    private Integer ram;

    @JsonProperty(value = "hdd")
    @Min(value = 10)
    @Max(value = 1000)
    private Integer hdd;

    @JsonIgnore
    private OperatingSystem operatingSystem;

    public VirtualMachineRequest() {
    }

    public VirtualMachineRequest(Integer cpuCores, Integer ram, Integer hdd, OperatingSystem operatingSystem) {
        this.cpuCores = cpuCores;
        this.ram = ram;
        this.hdd = hdd;
        this.operatingSystem = operatingSystem;
    }

    public static VirtualMachineRequest fromVirtualMachine(VirtualMachine virtualMachine) {
        return new VirtualMachineRequest(virtualMachine.getCpuCores(), virtualMachine.getRam(), virtualMachine.getHdd(),
                virtualMachine.getOperatingSystem());
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

    @JsonProperty(value = "operating_system")
    @ValueOfEnum(enumClass = OperatingSystem.class,
            message = INVALID_OPERATING_SYSTEM_MESSAGE)
    public String getOperatingSystemString() {
        return operatingSystem.name();
    }

    public void setOperatingSystemString(String operatingSystemString) {
        this.operatingSystem = OperatingSystem.valueOf(operatingSystemString);
    }

    @Override
    public String toString() {
        return "VirtualMachineRequest{" +
                "cpuCores=" + cpuCores +
                ", ram=" + ram +
                ", hdd=" + hdd +
                ", operatingSystem=" + operatingSystem +
                '}';
    }
}
