package com.doritech.microservice.AuthenticationService.ServiceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.doritech.microservice.AuthenticationService.Entity.EmployeeMaster;
import com.doritech.microservice.AuthenticationService.Entity.ResponseEntity;
import com.doritech.microservice.AuthenticationService.Entity.UserMaster;
import com.doritech.microservice.AuthenticationService.Exception.InvalidCredentialsException;
import com.doritech.microservice.AuthenticationService.Repository.EmployeeMasterRepository;
import com.doritech.microservice.AuthenticationService.Repository.UserMasterRepository;
import com.doritech.microservice.AuthenticationService.Request.LoginRequest;
import com.doritech.microservice.AuthenticationService.Service.AuthService;
import com.doritech.microservice.AuthenticationService.Service.JwtService;

@Service
public class AuthServiceImpl implements AuthService {

	private static final int DEFAULT_DAY_VALUE = 30;

	@Autowired
	private UserMasterRepository userRepo;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private JwtService jwtService;

	@Autowired
	private EmployeeMasterRepository employeeMasterRepository;

	@Override
	public ResponseEntity login(LoginRequest request) {

		UserMaster user = userRepo.findByLoginId(request.getLoginId())
				.orElseThrow(() -> new InvalidCredentialsException("Login failed. Invalid credentials"));

		String combined = request.getLoginId() + request.getPassword();
		if (!passwordEncoder.matches(combined, user.getPassword())) {
			throw new InvalidCredentialsException("Login failed. Invalid credentials");
		}

	
		int passwordExpiryDays = getIntParam();

		if (user.getLastLogin() != null) {
			LocalDateTime expiryDateTime = user.getLastLogin().plusDays(passwordExpiryDays);
			if (LocalDateTime.now().isAfter(expiryDateTime)) {
				return new ResponseEntity("Password expired. Please reset your password to continue.", 409, null);
			}
		}

		Integer employeeId = user.getSourceId();
		String employeeName = null;
		Integer compId = null;
		Integer siteId = null;

		if (employeeId != null) {
			EmployeeMaster emp = employeeMasterRepository.findByEmployeeId(employeeId)
					.orElseThrow(() -> new RuntimeException("Employee Not Found"));
			employeeName = emp.getEmployeeName();
			compId = emp.getCompanyId();
			siteId = emp.getSiteId();
		}

		String token = jwtService.generateTokenWithUserData(user, employeeName, compId, siteId);

		// 6. Update lastLogin AFTER expiry check — so next login uses this timestamp
		user.setLastLogin(LocalDateTime.now());
		userRepo.save(user);

		return new ResponseEntity("Login successful", 200, token);
	}

	public int getIntParam() {
		try {
			List<Object[]> params = userRepo.getCodeSerialDesp3();

			Optional<Object[]> match = params.stream()
					.filter(row -> "PASSWORD".equalsIgnoreCase(row[0] != null ? row[0].toString() : "")
							&& "EXPIRY_DAYS".equalsIgnoreCase(row[1] != null ? row[1].toString() : ""))
					.findFirst();

			if (match.isPresent() && match.get()[2] != null) {
				return Integer.parseInt(match.get()[2].toString().trim());
			}

		} catch (NumberFormatException e) {
		}
		return DEFAULT_DAY_VALUE;
	}

	@Override
	public ResponseEntity loginWithMobileNoAndOtp(String mobileNo) {
		
		ResponseEntity response = new ResponseEntity();
	    String mobileRegex = "^[6-9]\\d{9}$";

	    if (mobileNo == null || !mobileNo.matches(mobileRegex)) {
	    	response.setMessage("Invalid mobile number. Please enter a valid 10-digit mobile number.");
	    	response.setStatusCode(HttpStatus.BAD_REQUEST.value());
	        return response;
	    }
return null;
	    //EmployeeMaster employee = employeeMasterRepository.findByPhone(mobileNo)
	         //   .orElseThrow(() -> new InvalidCredentialsException("Mobile number is not registered."));
	    }
}