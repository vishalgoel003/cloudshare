package com.ripple.cloudshare.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ripple.cloudshare.data.entity.UserType;
import com.ripple.cloudshare.dto.validator.ValueOfEnum;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public class SignUpRequest {

    @NotNull(message = "name can not be empty")
    private String name;

    @NotNull(message = "email can not be empty")
    @Pattern(regexp = "^([a-zA-Z0-9_\\-\\.]+)@([a-zA-Z0-9_\\-\\.]+)\\.([a-zA-Z]{2,5})$",
        message = "email should be valid")
    private String email;

    @NotNull(message = "mobile can not be empty")
    @Pattern(regexp = "^[2-9]{2}\\d{8}$",
        message = "mobile should be valid 10 digit")
    private String mobile;

    @JsonProperty(value = "user_type")
    @ValueOfEnum(enumClass = UserType.class)
    private String userType = UserType.NON_ADMIN.name();

    @NotNull(message = "password can not be empty")
    private String password;

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "SignUpRequest{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", mobile='" + mobile + '\'' +
                ", userType='" + userType + '\'' +
                '}';
    }
}
