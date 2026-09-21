package com.doritech.tmsservice.serviceImpl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.doritech.tmsservice.config.CurrentUser;
import com.doritech.tmsservice.request.SecurityLogRequest;
import com.doritech.tmsservice.response.PageResponse;
import com.doritech.tmsservice.response.SecurityLogResponse;
import com.doritech.tmsservice.service.SecurityLogService;
import com.doritech.tmsservice.tms.entity.ResponseEntity;
import com.doritech.tmsservice.tms.entity.SecurityLog;
import com.doritech.tmsservice.tms.repository.SecurityLogRepository;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class SecurityLogServiceImpl implements SecurityLogService {

	private final SecurityLogRepository securityLogRepository;

	public SecurityLogServiceImpl(SecurityLogRepository securityLogRepository) {
		this.securityLogRepository = securityLogRepository;
	}

	@Override
	@Transactional("tmsTransactionManager")
	public ResponseEntity createSecurityLog(SecurityLogRequest request, HttpServletRequest httpRequest) {

		try {

			if (request == null) {
				return new ResponseEntity("Security log data is required", HttpStatus.BAD_REQUEST.value(), null);
			}

			if (request.getViolationType() == null) {
				return new ResponseEntity("Violation type is required", HttpStatus.BAD_REQUEST.value(), null);
			}

			Long userId = CurrentUser.getUserId();

			if (userId == null || userId <= 0) {
				return new ResponseEntity("Invalid user", HttpStatus.BAD_REQUEST.value(), null);
			}

			SecurityLog securityLog = new SecurityLog();

			securityLog.setUserId(userId);

			securityLog.setSessionId(request.getSessionId());

			securityLog.setViolationType(request.getViolationType());

			securityLog.setDetails(request.getDetails());

			String ipAddress = httpRequest.getHeader("X-Forwarded-For");

			if (ipAddress == null || ipAddress.isBlank()) {
				ipAddress = httpRequest.getRemoteAddr();
			}

			if (ipAddress != null && ipAddress.contains(",")) {
				ipAddress = ipAddress.split(",")[0].trim();
			}

			securityLog.setIpAddress(ipAddress);

			String userAgent = httpRequest.getHeader("User-Agent");

			securityLog.setUserAgent(userAgent);

			securityLog.setDeviceInfo(request.getDeviceInfo());

			securityLog.setTestAttemptId(request.getTestAttemptId());

			securityLog.setWarningShown(request.getWarningShown() != null ? request.getWarningShown() : false);

			SecurityLog savedSecurityLog = securityLogRepository.save(securityLog);

			SecurityLogResponse response = mapToResponse(savedSecurityLog);

			return new ResponseEntity("Security log created successfully", HttpStatus.CREATED.value(), response);

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Internal server error!", HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	@Transactional(value = "tmsTransactionManager", readOnly = true)
	public ResponseEntity getAllSecurityLogs(int page, int size, String sortBy, String sortDir) {

		try {

			if (page < 0) {
				return new ResponseEntity("Page number cannot be negative", HttpStatus.BAD_REQUEST.value(), null);
			}

			if (size <= 0) {
				return new ResponseEntity("Page size must be greater than zero", HttpStatus.BAD_REQUEST.value(), null);
			}

			if (sortBy == null || sortBy.trim().isEmpty()) {
				sortBy = "securityLogId";
			}

			if (sortDir == null || sortDir.trim().isEmpty()) {
				sortDir = "desc";
			}

			Sort sort;

			if (sortDir.equalsIgnoreCase("asc")) {

				sort = Sort.by(sortBy).ascending();

			} else if (sortDir.equalsIgnoreCase("desc")) {

				sort = Sort.by(sortBy).descending();

			} else {

				return new ResponseEntity("Invalid sort direction. Use asc or desc", HttpStatus.BAD_REQUEST.value(),
						null);
			}

			Pageable pageable = PageRequest.of(page, size, sort);

			Page<SecurityLog> securityLogPage = securityLogRepository.findAll(pageable);

			List<SecurityLogResponse> response = securityLogPage.getContent().stream().map(this::mapToResponse)
					.collect(Collectors.toList());

			PageResponse<SecurityLogResponse> pageResponse = new PageResponse<>(response, securityLogPage.getNumber(),
					securityLogPage.getSize(), securityLogPage.getTotalElements(), securityLogPage.getTotalPages(),
					securityLogPage.isLast());

			return new ResponseEntity("Security logs fetched successfully", HttpStatus.OK.value(), pageResponse);

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Internal server error!", HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	private SecurityLogResponse mapToResponse(SecurityLog securityLog) {
		SecurityLogResponse response = new SecurityLogResponse();
		response.setSecurityLogId(securityLog.getSecurityLogId());
		response.setUserId(securityLog.getUserId());
		response.setSessionId(securityLog.getSessionId());
		response.setViolationType(securityLog.getViolationType());
		response.setDetails(securityLog.getDetails());
		response.setIpAddress(securityLog.getIpAddress());
		response.setUserAgent(securityLog.getUserAgent());
		response.setDeviceInfo(securityLog.getDeviceInfo());
		response.setTestAttemptId(securityLog.getTestAttemptId());
		response.setTimestamp(securityLog.getTimestamp());
		response.setWarningShown(securityLog.getWarningShown());

		return response;
	}
}