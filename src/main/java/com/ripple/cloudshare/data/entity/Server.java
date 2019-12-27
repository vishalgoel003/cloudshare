package com.ripple.cloudshare.data.entity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "server")
public class Server {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "server_name", nullable = false)
    private String serverName;

    @Column(name = "shared_memory", nullable = false)
    private Integer sharedMemory;

    @Column(name = "shared_disk", nullable = false)
    private Integer sharedDisk;

    @Column(name = "shared_cpu_cores", nullable = false)
    private Integer sharedCpuCores;

    @OneToMany(mappedBy = "server", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<VirtualMachine> virtualMachines;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public Integer getSharedMemory() {
        return sharedMemory;
    }

    public void setSharedMemory(Integer sharedMemory) {
        this.sharedMemory = sharedMemory;
    }

    public Integer getSharedDisk() {
        return sharedDisk;
    }

    public void setSharedDisk(Integer sharedDisk) {
        this.sharedDisk = sharedDisk;
    }

    public Integer getSharedCpuCores() {
        return sharedCpuCores;
    }

    public void setSharedCpuCores(Integer sharedCpuCores) {
        this.sharedCpuCores = sharedCpuCores;
    }

    public List<VirtualMachine> getVirtualMachines() {
        return virtualMachines;
    }

    public void setVirtualMachines(List<VirtualMachine> virtualMachines) {
        this.virtualMachines = virtualMachines;
    }

    public void addVirtualMachine(VirtualMachine virtualMachine) {
        if(this.virtualMachines == null){
            this.virtualMachines = new ArrayList<>();
        }
        this.virtualMachines.add(virtualMachine);
        virtualMachine.setServer(this);
    }
}
