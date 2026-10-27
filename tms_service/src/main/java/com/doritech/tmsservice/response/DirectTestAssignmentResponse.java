
package com.doritech.tmsservice.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.doritech.tmsservice.enums.AssignmentStatus;

public class DirectTestAssignmentResponse {

	private Long directTestAssignmentId;
	private Long testSetId;
	private String testName;
	private Long userId;
	private String userName;
	private Long batchId;
	private String batchName;
	private Long assignedBy;
	private String assignedByName;
	private LocalDateTime assignedAt;
	private LocalDateTime dueDate;
	private AssignmentStatus status;
	private BigDecimal progressPercentage;
	private LocalDateTime completionDate;
	private BigDecimal finalScore;
	private Boolean isPassed;
	private Boolean certificateGenerated;
	private Integer attemptedQuestions;
	private Integer correctAnswers;
	private Integer wrongAnswers;
	private Integer totalQuestions;
	private String remarks;
	private LocalDateTime startedAt;

	public Long getDirectTestAssignmentId() {
		return directTestAssignmentId;
	}

	public void setDirectTestAssignmentId(Long v) {
		directTestAssignmentId = v;
	}

	public Long getTestSetId() {
		return testSetId;
	}

	public void setTestSetId(Long v) {
		testSetId = v;
	}

	public String getTestName() {
		return testName;
	}

	public void setTestName(String v) {
		testName = v;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long v) {
		userId = v;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String v) {
		userName = v;
	}

	public Long getAssignedBy() {
		return assignedBy;
	}

	public void setAssignedBy(Long v) {
		assignedBy = v;
	}

	public String getAssignedByName() {
		return assignedByName;
	}

	public void setAssignedByName(String v) {
		assignedByName = v;
	}

	public LocalDateTime getAssignedAt() {
		return assignedAt;
	}

	public void setAssignedAt(LocalDateTime v) {
		assignedAt = v;
	}

	public LocalDateTime getDueDate() {
		return dueDate;
	}

	public void setDueDate(LocalDateTime v) {
		dueDate = v;
	}

	public AssignmentStatus getStatus() {
		return status;
	}

	public void setStatus(AssignmentStatus v) {
		status = v;
	}

	public BigDecimal getProgressPercentage() {
		return progressPercentage;
	}

	public void setProgressPercentage(BigDecimal v) {
		progressPercentage = v;
	}

	public LocalDateTime getCompletionDate() {
		return completionDate;
	}

	public void setCompletionDate(LocalDateTime v) {
		completionDate = v;
	}

	public BigDecimal getFinalScore() {
		return finalScore;
	}

	public void setFinalScore(BigDecimal v) {
		finalScore = v;
	}

	public Boolean getIsPassed() {
		return isPassed;
	}

	public void setIsPassed(Boolean v) {
		isPassed = v;
	}

	public Boolean getCertificateGenerated() {
		return certificateGenerated;
	}

	public void setCertificateGenerated(Boolean v) {
		certificateGenerated = v;
	}

	public Integer getAttemptedQuestions() {
		return attemptedQuestions;
	}

	public void setAttemptedQuestions(Integer v) {
		attemptedQuestions = v;
	}

	public Integer getCorrectAnswers() {
		return correctAnswers;
	}

	public void setCorrectAnswers(Integer v) {
		correctAnswers = v;
	}

	public Integer getWrongAnswers() {
		return wrongAnswers;
	}

	public void setWrongAnswers(Integer v) {
		wrongAnswers = v;
	}

	public Integer getTotalQuestions() {
		return totalQuestions;
	}

	public void setTotalQuestions(Integer v) {
		totalQuestions = v;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String v) {
		remarks = v;
	}

	public LocalDateTime getStartedAt() {
		return startedAt;
	}

	public void setStartedAt(LocalDateTime v) {
		startedAt = v;
	}

	public Long getBatchId() {
		return batchId;
	}

	public void setBatchId(Long batchId) {
		this.batchId = batchId;
	}

	public String getBatchName() {
		return batchName;
	}

	public void setBatchName(String batchName) {
		this.batchName = batchName;
	}

}
