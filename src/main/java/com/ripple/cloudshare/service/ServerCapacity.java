package com.ripple.cloudshare.service;

import com.ripple.cloudshare.data.entity.Server;
import com.ripple.cloudshare.data.entity.VirtualMachine;
import com.ripple.cloudshare.exception.RippleAppRuntimeException;

public class ServerCapacity implements Cloneable {

    private Integer memory;
    private Integer disk;
    private Integer cpuCores;

    public ServerCapacity() {}

    public static ServerCapacity fromServer(Server server){
        return new ServerCapacity(server.getSharedMemory(), server.getSharedDisk(), server.getSharedCpuCores());
    }

    public static ServerCapacity fromVirtualMachine(VirtualMachine virtualMachine){
        return new ServerCapacity(virtualMachine.getRam(), virtualMachine.getHdd(), virtualMachine.getCpuCores());
    }

    public ServerCapacity(Integer memory, Integer disk, Integer cpuCores) {
        this.memory = memory;
        this.disk = disk;
        this.cpuCores = cpuCores;
    }

    public boolean canServeRequest(VirtualMachineRequest reserveRequest) {
        if (this.memory < reserveRequest.getRam() || this.disk < reserveRequest.getHdd() || this.cpuCores < reserveRequest.getCpuCores()) {
            return false;
        }
        return true;
    }

    public synchronized void consumeCapacity(VirtualMachineRequest provisioningRequest) {
        if (this.memory < provisioningRequest.getRam() || this.disk < provisioningRequest.getHdd() || this.cpuCores < provisioningRequest.getCpuCores()) {
            throw new RippleAppRuntimeException("Requested server can not provision the VM request");
        }
        this.memory -= provisioningRequest.getRam();
        this.disk -= provisioningRequest.getHdd();
        this.cpuCores -= provisioningRequest.getCpuCores();
    }

    public synchronized void addCapacity(ServerCapacity serverCapacity) {
        this.memory += serverCapacity.memory;
        this.disk += serverCapacity.disk;
        this.cpuCores += serverCapacity.cpuCores;
    }

    public Integer getMemory() {
        return memory;
    }

    public void setMemory(Integer memory) {
        this.memory = memory;
    }

    public Integer getDisk() {
        return disk;
    }

    public void setDisk(Integer disk) {
        this.disk = disk;
    }

    public Integer getCpuCores() {
        return cpuCores;
    }

    public void setCpuCores(Integer cpuCores) {
        this.cpuCores = cpuCores;
    }

    @Override
    public ServerCapacity clone() {
        final ServerCapacity clone;
        try {
            clone = (ServerCapacity) super.clone();
        }
        catch (CloneNotSupportedException ex) {
            throw new RuntimeException("Could not clone object", ex);
        }
        //immutable fields
        clone.memory = this.memory;
        clone.disk = this.disk;
        clone.cpuCores = this.cpuCores;
        return clone;
    }

    public void clear() {
        this.memory = 0;
        this.disk = 0;
        this.cpuCores = 0;
    }

    @Override
    public String toString() {
        return "ServerCapacity{" +
                "memory=" + memory +
                ", disk=" + disk +
                ", cpuCores=" + cpuCores +
                '}';
    }
}
