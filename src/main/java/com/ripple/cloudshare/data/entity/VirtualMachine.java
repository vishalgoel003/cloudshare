package com.ripple.cloudshare.data.entity;

import javax.persistence.*;

@Entity
@Table(name = "virtual_machine")
public class VirtualMachine {

    @Id
    @GeneratedValue(generator = "virtual_machine_id_generator")
    @SequenceGenerator(
            name = "virtual_machine_id_generator",
            sequenceName = "virtual_machine_id"
    )
    private Long id;

    @Column(name = "operating_system", nullable = false)
    @Enumerated(EnumType.STRING)
    private OperatingSystem operatingSystem;

    @Column(name = "ram", nullable = false)
    private Integer ram;

    @Column(name = "hdd", nullable = false)
    private Integer hdd;

    @Column(name = "cpu_cores", nullable = false)
    private Integer cpuCores;

    @Column(name = "removed", nullable = false)
    private Boolean removed = Boolean.FALSE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "server_id", nullable = false)
    private Server server;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OperatingSystem getOperatingSystem() {
        return operatingSystem;
    }

    public void setOperatingSystem(OperatingSystem operatingSystem) {
        this.operatingSystem = operatingSystem;
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

    public Integer getCpuCores() {
        return cpuCores;
    }

    public void setCpuCores(Integer cpuCores) {
        this.cpuCores = cpuCores;
    }

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Boolean getRemoved() {
        return removed;
    }

    public void setRemoved(Boolean deleted) {
        this.removed = deleted;
    }
}
