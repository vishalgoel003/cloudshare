package com.ripple.cloudshare.dto.response;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.ripple.cloudshare.data.entity.User;

public class SignUpResponse {

    private Long id;
    private String name;
    private String email;
    private String mobile;
    @JsonProperty(value = "user_type")
    private String userType;

    public static SignUpResponse fromUserEntity(User userEntity){
        SignUpResponse signUpResponse = new SignUpResponse();
        signUpResponse.setId(userEntity.getId());
        signUpResponse.setEmail(userEntity.getEmail());
        signUpResponse.setMobile(userEntity.getMobile());
        signUpResponse.setName(userEntity.getName());
        signUpResponse.setUserType(userEntity.getUserType().name());
        return signUpResponse;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    @Override
    public String toString() {
        return "SignUpResponse{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", mobile='" + mobile + '\'' +
                ", userType='" + userType + '\'' +
                '}';
    }
}
