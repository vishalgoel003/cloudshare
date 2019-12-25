package com.ripple.cloudshare.dto.response;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.ripple.cloudshare.data.entity.User;

public class SignInResponse {

    @JsonProperty(value = "authorization_token")
    private String authorizationToken;

    public static SignInResponse fromAuthorizationToken(String authorizationToken){
        SignInResponse response = new SignInResponse();
        response.setAuthorizationToken(authorizationToken);
        return response;
    }

    public String getAuthorizationToken() {
        return authorizationToken;
    }

    public void setAuthorizationToken(String authorizationToken) {
        this.authorizationToken = authorizationToken;
    }
}
