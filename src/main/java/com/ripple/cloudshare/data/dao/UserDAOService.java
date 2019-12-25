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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserDAOService {

    private static final String CLASS_NAME = "UserDAOService";

    private final UserRepository userRepository;
    private final Logger logger;

    @Autowired
    public UserDAOService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.logger = LoggerFactory.getLogger(CLASS_NAME);
    }

    public User createUser(SignUpRequest signUpRequest){
        Long conflictingRecords = userRepository.findRecordsMatchingDetails(signUpRequest.getEmail(), signUpRequest.getMobile());
        if(0L < conflictingRecords) {
            logger.info("Unique constraint not satisfied");
            throw new RippleUserRuntimeException("email or mobile already in use");
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
            throw new RippleAppRuntimeException("Something went wrong, please retry after some time");
        }

        return user;
    }

    public Long validateLoginAndReturnUserId(String email, String password){
        User user = userRepository.findByEmail(email);

        if (user == null){
            logger.error("No user exists with given email: " + email);
            throw new RippleUserRuntimeException("No user exists with given email", HttpStatus.BAD_REQUEST);
        } else if (!user.getPassword().equals(password)) {
            logger.error("Incorrect password for user with email" + email);
            throw new RippleUserRuntimeException("Incorrect password", HttpStatus.FORBIDDEN);
        }

        return user.getId();
    }

    public User getByEmail(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null){
            logger.error("No user exists with given email: " + email);
            throw new RippleUserRuntimeException("No user exists with given email", HttpStatus.BAD_REQUEST);
        }
        return user;
    }

    public User getById(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if(!optionalUser.isPresent() || optionalUser.get().getDeleted()) {
            logger.error("No user exists with given id: " + id);
            throw new RippleUserRuntimeException("No user exists with given id", HttpStatus.BAD_REQUEST);
        }
        return optionalUser.get();
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User deleteById(Long id) {
        User user = getById(id);
        if (user.getUserType().equals(UserType.ADMIN)){
            logger.error("Attempt to delete admin user id: " + id);
            throw new RippleUserRuntimeException("Can not delete admin user with given id", HttpStatus.BAD_REQUEST);
        }
        user.setDeleted(true); //soft delete
        user = userRepository.save(user);
        //TODO: call delayed async method to decommission related machines and hard delete user
        return user;
    }
}
