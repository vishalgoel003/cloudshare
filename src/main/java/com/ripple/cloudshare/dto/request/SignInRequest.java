package com.ripple.cloudshare.dto.request;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import static com.ripple.cloudshare.ApplicationConstants.EMAIL_VALIDATION_REGEX;

public class SignInRequest {

    @NotNull(message = "email can not be empty")
    @Pattern(regexp = EMAIL_VALIDATION_REGEX,
            message = "email should be valid")
    private String email;

    @NotNull(message = "password can not be empty")
    private String password;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public SignInRequest(@NotNull(message = "email can not be empty") @Pattern(regexp = EMAIL_VALIDATION_REGEX,
            message = "email should be valid") String email, @NotNull(message = "password can not be empty") String password) {
        this.email = email;
        this.password = password;
    }

    @Override
    public String toString() {
        return "SignInRequest{" +
                "email='" + email + '\'' +
                '}';
    }
}
