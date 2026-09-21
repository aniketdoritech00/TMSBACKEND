package com.doritech.tmsservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.doritech.tmsservice.request.SecurityLogRequest;
import com.doritech.tmsservice.service.SecurityLogService;
import com.doritech.tmsservice.tms.entity.ResponseEntity;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/tms/security-log")
public class SecurityLogController {

	private final SecurityLogService securityLogService;

	public SecurityLogController(SecurityLogService securityLogService) {
		this.securityLogService = securityLogService;
	}

	@PostMapping("/createSecurityLog")
	public ResponseEntity createSecurityLog(@RequestBody SecurityLogRequest request, HttpServletRequest httpRequest) {
		return securityLogService.createSecurityLog(request, httpRequest);
	}

	@GetMapping("/getAllSecurityLogs")
	public ResponseEntity getAllSecurityLogs(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "securityLogId") String sortBy,
			@RequestParam(defaultValue = "desc") String sortDir) {
		return securityLogService.getAllSecurityLogs(page, size, sortBy, sortDir);
	}
}