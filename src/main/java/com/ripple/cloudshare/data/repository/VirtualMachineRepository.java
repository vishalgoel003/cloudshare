package com.ripple.cloudshare.data.repository;

import com.ripple.cloudshare.data.entity.VirtualMachine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VirtualMachineRepository extends JpaRepository<VirtualMachine, Long> {

}
