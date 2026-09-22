package com.doritech.tmsservice.service;

import java.time.LocalDateTime;

import com.doritech.tmsservice.enums.SecurityViolationType;
import com.doritech.tmsservice.request.SecurityLogRequest;
import com.doritech.tmsservice.tms.entity.ResponseEntity;

import jakarta.servlet.http.HttpServletRequest;

public interface SecurityLogService {

	ResponseEntity createSecurityLog(SecurityLogRequest request, HttpServletRequest httpRequest);

	ResponseEntity getAllSecurityLogs(int page, int size, String sortBy, String sortDir);

	ResponseEntity getAllSecurityLogsFilter(int page, int size, String sortBy, String sortDir,
			SecurityViolationType violationType, Boolean warningShown, LocalDateTime fromDate, LocalDateTime toDate);

}