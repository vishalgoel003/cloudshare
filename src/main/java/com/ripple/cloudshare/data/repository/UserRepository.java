package com.ripple.cloudshare.data.repository;

import com.ripple.cloudshare.data.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query(value = "SELECT COUNT(user) FROM User user WHERE user.email=:email or user.mobile=:mobile")
    long findRecordsMatchingDetails(String email, String mobile);

    User findByEmail(String email);

    Optional<User> getByEmail(String username);

    @Query(value = "SELECT machine.user FROM VirtualMachine machine WHERE machine.id=:virtualMachineId")
    Optional<User> getUserByMachineId(Long virtualMachineId);
}
