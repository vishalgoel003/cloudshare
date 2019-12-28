package com.ripple.cloudshare.data.repository;

import com.ripple.cloudshare.data.entity.VirtualMachine;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface VirtualMachineRepository extends JpaRepository<VirtualMachine, Long> {

    @Query(value = "from VirtualMachine where user.id = :userId")
    List<VirtualMachine> findAllByUserId(Long userId);

    @Query(value = "from VirtualMachine where user.id = :userId order by ram DESC")
    List<VirtualMachine> findAllByUserIdOrderByRam(Long userId);

    @Query(value = "from VirtualMachine where user.id = :userId order by ram DESC")
    List<VirtualMachine> findAllByUserIdOrderByRamWithPaging(Long userId, Pageable pageable);

    @Query(value = "from VirtualMachine order by ram DESC")
    List<VirtualMachine> findAllWithPaging(Pageable pageable);
}
