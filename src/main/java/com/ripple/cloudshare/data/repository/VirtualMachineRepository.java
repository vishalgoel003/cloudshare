package com.ripple.cloudshare.data.repository;

import com.ripple.cloudshare.data.entity.VirtualMachine;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface VirtualMachineRepository extends JpaRepository<VirtualMachine, Long> {

    @Query(value = "from VirtualMachine where user.id = :userId and removed = FALSE")
    List<VirtualMachine> findAllLiveByUserId(Long userId);

    @Query(value = "SELECT vm from VirtualMachine vm where vm.id = :vmId and vm.removed = FALSE")
    Optional<VirtualMachine> findLiveOneById(Long vmId);

    @Query(value = "SELECT vm from VirtualMachine vm where vm.id = :vmId and vm.user.id = :userId and vm.removed = FALSE")
    Optional<VirtualMachine> findLiveOneByIdAndUserId(Long vmId, Long userId);

    @Query(value = "from VirtualMachine where user.id = :userId and removed = FALSE order by ram DESC")
    List<VirtualMachine> findAllLiveByUserIdOrderByRamWithPaging(Long userId, Pageable pageable);

    @Query(value = "from VirtualMachine WHERE removed = FALSE order by ram DESC")
    List<VirtualMachine> findAllLiveOrderByRamWithPaging(Pageable pageable);

    @Transactional
    @Modifying
    @Query(value = "update VirtualMachine set removed = TRUE where id = :virtualMachineId")
    int softDeleteVirtualMachine(Long virtualMachineId);
}
