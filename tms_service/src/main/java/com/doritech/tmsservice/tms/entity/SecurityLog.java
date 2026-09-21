package com.doritech.tmsservice.tms.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.doritech.tmsservice.enums.SecurityViolationType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "security_logs")
public class SecurityLog {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "security_log_id")
	private Long securityLogId;

	@Column(name = "user_id", nullable = false)
	private Long userId;

	@Column(name = "session_id", length = 255)
	private String sessionId;

	@Enumerated(EnumType.STRING)
	@Column(name = "violation_type", nullable = false)
	private SecurityViolationType violationType;

	@Column(name = "details", columnDefinition = "json")
	private String details;

	@Column(name = "ip_address", length = 45)
	private String ipAddress;

	@Column(name = "user_agent", length = 255)
	private String userAgent;

	@Column(name = "device_info", length = 255)
	private String deviceInfo;

	@Column(name = "test_attempt_id")
	private Long testAttemptId;

	@CreationTimestamp
	@Column(name = "timestamp", updatable = false)
	private LocalDateTime timestamp;

	@Column(name = "warning_shown")
	private Boolean warningShown = false;

	public Long getSecurityLogId() {
		return securityLogId;
	}

	public void setSecurityLogId(Long securityLogId) {
		this.securityLogId = securityLogId;
	}

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

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}

	public Boolean getWarningShown() {
		return warningShown;
	}

	public void setWarningShown(Boolean warningShown) {
		this.warningShown = warningShown;
	}
}