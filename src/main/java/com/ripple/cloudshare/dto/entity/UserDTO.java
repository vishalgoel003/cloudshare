package com.ripple.cloudshare.dto.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ripple.cloudshare.data.entity.User;

public class UserDTO {

    private Long id;

    private String name;

    private String email;

    private String mobile;

    @JsonProperty(value = "user_type")
    private String userType;

    public UserDTO(Long id, String name, String email, String mobile, String userType) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.mobile = mobile;
        this.userType = userType;
    }

    public static UserDTO fromUser(User user){
        return new UserDTO(user.getId(), user.getName(), user.getEmail(), user.getMobile(), user.getUserType().name());
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
        return "UserDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", mobile='" + mobile + '\'' +
                ", userType='" + userType + '\'' +
                '}';
    }
}
