package com.ripple.cloudshare.data.dao;

import com.ripple.cloudshare.data.entity.User;
import com.ripple.cloudshare.data.entity.UserType;
import com.ripple.cloudshare.data.repository.UserRepository;
import com.ripple.cloudshare.dto.request.SignUpRequest;
import com.ripple.cloudshare.dto.response.SignUpResponse;
import com.ripple.cloudshare.exception.RippleAppRuntimeException;
import com.ripple.cloudshare.exception.RippleUserRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public SignUpResponse createUser(SignUpRequest signUpRequest){
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

        SignUpResponse signUpResponse = SignUpResponse.fromUserEntity(user);
        return signUpResponse;
    }
}
