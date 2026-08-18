package com.doritech.microservice.AuthenticationService.Service;

import com.doritech.microservice.AuthenticationService.Entity.ResponseEntity;
import com.doritech.microservice.AuthenticationService.Request.LoginRequest;

public interface AuthService {

	ResponseEntity login(LoginRequest request);

	ResponseEntity loginWithMobileNoAndOtp(String mobileNo);

}
