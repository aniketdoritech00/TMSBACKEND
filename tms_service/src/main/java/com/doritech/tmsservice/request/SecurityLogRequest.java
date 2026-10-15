package com.doritech.tmsservice.request;

import com.doritech.tmsservice.enums.SecurityViolationType;

public class SecurityLogRequest {

	private Long userId;

	private String sessionId;

	private SecurityViolationType violationType;

	private String details;

	private String ipAddress;

	private String userAgent;

	private String deviceInfo;

	private Long testAttemptId;

	private Boolean warningShown = false;

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public SecurityViolationType getViolationType() {
		return violationType;
	}

	public void setViolationType(SecurityViolationType violationType) {
		this.violationType = violationType;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	public String getDeviceInfo() {
		return deviceInfo;
	}

	public void setDeviceInfo(String deviceInfo) {
		this.deviceInfo = deviceInfo;
	}

	public Long getTestAttemptId() {
		return testAttemptId;
	}

	public void setTestAttemptId(Long testAttemptId) {
		this.testAttemptId = testAttemptId;
	}

	public Boolean getWarningShown() {
		return warningShown;
	}

	public void setWarningShown(Boolean warningShown) {
		this.warningShown = warningShown;
	}
}