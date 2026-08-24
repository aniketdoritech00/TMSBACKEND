package com.doritech.microservice.AuthenticationService.Request;

import jakarta.validation.constraints.NotBlank;

public class LoginRequest {

    @NotBlank(message = "{loginId.required}")
    private String loginId;

    @NotBlank(message = "{password.required}")
    private String password;

    public LoginRequest() {}

    public LoginRequest(String loginId, String password) {
        this.loginId = loginId;
        this.password = password;
    }

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}