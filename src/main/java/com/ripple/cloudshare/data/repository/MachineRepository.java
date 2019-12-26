package com.ripple.cloudshare.data.repository;

import com.ripple.cloudshare.data.entity.Machine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MachineRepository extends JpaRepository<Machine, Long> {

}
