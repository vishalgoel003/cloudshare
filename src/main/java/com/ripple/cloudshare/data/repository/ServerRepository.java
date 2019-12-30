package com.ripple.cloudshare.data.repository;

import com.ripple.cloudshare.data.entity.Server;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServerRepository extends JpaRepository<Server, Long> {

}
