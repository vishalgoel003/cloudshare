package com.ripple.cloudshare.data.dao;

import com.ripple.cloudshare.data.entity.User;
import com.ripple.cloudshare.data.entity.UserType;
import com.ripple.cloudshare.data.repository.UserRepository;
import com.ripple.cloudshare.dto.request.SignUpRequest;
import com.ripple.cloudshare.exception.RippleAppRuntimeException;
import com.ripple.cloudshare.exception.RippleUserRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.ripple.cloudshare.ApplicationConstants.*;

@Service
public class UserDAOService {

    private static final String CLASS_NAME = "UserDAOService";

    private final UserRepository userRepository;
    private final VirtualMachineDAOService virtualMachineDAOService;

    private final Logger logger;

    @Autowired
    public UserDAOService(UserRepository userRepository, VirtualMachineDAOService virtualMachineDAOService) {
        this.userRepository = userRepository;
        this.virtualMachineDAOService = virtualMachineDAOService;
        this.logger = LoggerFactory.getLogger(CLASS_NAME);
    }

    @Caching(evict = {
            @CacheEvict(value = "users", allEntries = true)
    }, put = {
            @CachePut(value = "user_by_id", key = "#result.id")
    })
    public User createUser(SignUpRequest signUpRequest){
        long conflictingRecords = userRepository.findRecordsMatchingDetails(signUpRequest.getEmail(), signUpRequest.getMobile());
        if(0L < conflictingRecords) {
            logger.info("Unique constraint not satisfied");
            throw new RippleUserRuntimeException(EMAIL_OR_MOBILE_ALREADY_IN_USE);
        }

        User user = new User();
        user.setUserType(UserType.valueOf(signUpRequest.getUserType()));
        user.setPassword(signUpRequest.getPassword());
        user.setEmail(signUpRequest.getEmail());
        user.setName(signUpRequest.getName());
        user.setMobile(signUpRequest.getMobile());

        try {
            user = userRepository.save(user);
        } catch (Exception e){
            logger.error("Error while saving the record", e);
            throw new RippleAppRuntimeException(SOMETHING_WENT_WRONG);
        }

        return user;
    }

    public User getByEmail(String email) {
        logger.info("Getting user info by email");
        User user = userRepository.findByEmail(email);
        if (user == null){
            logger.error("No user exists with given email: " + email);
            throw new RippleUserRuntimeException(NO_USER_WITH_GIVEN_EMAIL, HttpStatus.BAD_REQUEST);
        }
        return user;
    }

    @Cacheable(value = "user_by_id", key = "#id")
    public User getById(Long id) {
        logger.info("Not using cache");

        Optional<User> optionalUser = userRepository.findById(id);
        if(!optionalUser.isPresent() || optionalUser.get().getDeleted()) {
            logger.error("No user exists with given id: " + id);
            throw new RippleUserRuntimeException(NO_USER_WITH_GIVEN_ID, HttpStatus.BAD_REQUEST);
        }
        return optionalUser.get();
    }

    @Cacheable(value = "users")
    public List<User> getAllUsers() {
        logger.info("Not using cache");
        return userRepository.findAll();
    }

    @Caching(evict = {
            @CacheEvict(value = "users", allEntries = true),
            @CacheEvict(value = "user_by_id", key = "#id")
    })
    public User deleteById(Long id) {
        User user = getById(id);
        if (user.getUserType().equals(UserType.ADMIN)){
            logger.error("Attempt to delete admin user id: " + id);
            throw new RippleUserRuntimeException(ADMIN_CAN_NOT_DELETE_OTHER_ADMIN, HttpStatus.BAD_REQUEST);
        }
        user.setDeleted(true); //soft delete
        user = userRepository.save(user);
        virtualMachineDAOService.deleteAllMachinesForUser(user);
        return user;
    }

}
