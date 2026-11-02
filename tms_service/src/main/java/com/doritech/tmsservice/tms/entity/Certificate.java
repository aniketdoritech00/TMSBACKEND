
package com.doritech.tmsservice.tms.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "certificates")
public class Certificate {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "certificate_id")
	private Long certificateId;

	@Column(name = "certificate_number", nullable = false, unique = true)
	private String certificateNumber;

	@Column(name = "test_attempt_id", nullable = false, unique = true)
	private Long testAttemptId;

	@Column(name = "user_id", nullable = false)
	private Long userId;

	@Column(name = "test_set_id", nullable = false)
	private Long testSetId;

	@Column(name = "user_name", nullable = false)
	private String userName;

	@Column(name = "test_name", nullable = false)
	private String testName;

	@Column(name = "issue_date", nullable = false)
	private LocalDate issueDate;

	@Lob
	@Column(name = "certificate_pdf", nullable = false, columnDefinition = "LONGBLOB")
	private byte[] certificatePdf;

	@Column(name = "created_at", updatable = false)
	private LocalDateTime createdAt;

	@PrePersist
	protected void onCreate() {
		createdAt = LocalDateTime.now();
	}

	public Long getCertificateId() {
		return certificateId;
	}

	public void setCertificateId(Long certificateId) {
		this.certificateId = certificateId;
	}

	public String getCertificateNumber() {
		return certificateNumber;
	}

	public void setCertificateNumber(String certificateNumber) {
		this.certificateNumber = certificateNumber;
	}

	public Long getTestAttemptId() {
		return testAttemptId;
	}

	public void setTestAttemptId(Long testAttemptId) {
		this.testAttemptId = testAttemptId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getTestSetId() {
		return testSetId;
	}

	public void setTestSetId(Long testSetId) {
		this.testSetId = testSetId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getTestName() {
		return testName;
	}

	public void setTestName(String testName) {
		this.testName = testName;
	}

	public LocalDate getIssueDate() {
		return issueDate;
	}

	public void setIssueDate(LocalDate issueDate) {
		this.issueDate = issueDate;
	}

	public byte[] getCertificatePdf() {
		return certificatePdf;
	}

	public void setCertificatePdf(byte[] certificatePdf) {
		this.certificatePdf = certificatePdf;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
}
