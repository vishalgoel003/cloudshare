package com.ripple.cloudshare.data.repository;

import com.ripple.cloudshare.data.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {

    @Query(
            value = "SELECT COUNT(*) FROM User WHERE EMAIL = ?1 OR MOBILE = ?2",
            nativeQuery = true
    )
    long findRecordsMatchingDetails(String email, String mobile);

}
