package com.ripple.cloudshare.dto.response;


import com.ripple.cloudshare.controller.UserController;
import com.ripple.cloudshare.data.entity.User;

public class SignUpResponse {

    private Long id;
    private String path;

    public static SignUpResponse fromUserEntity(User userEntity){
        SignUpResponse signUpResponse = new SignUpResponse();
        signUpResponse.setId(userEntity.getId());
        signUpResponse.setPath(String.format("/%s/%d", UserController.USER_CONTROLLER_ROOT, userEntity.getId()));
        return signUpResponse;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "SignUpResponse{" +
                "id=" + id +
                ", path='" + path + '\'' +
                '}';
    }
}
