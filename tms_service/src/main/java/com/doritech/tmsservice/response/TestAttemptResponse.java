package com.doritech.tmsservice.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.doritech.tmsservice.enums.TestAttemptStatus;
import com.doritech.tmsservice.enums.TestResult;

public class TestAttemptResponse {

	private Long testAttemptId;

	private Long testSetId;

	private Long userId;

	private Long trainingAssignmentId;

	private LocalDateTime startTime;

	private LocalDateTime endTime;

	private BigDecimal totalScore;

	private Integer totalQuestions;

	private Integer correctAnswers;

	private Integer wrongAnswers;

	private Integer skippedQuestions;

	private BigDecimal passingPercentage;

	private BigDecimal percentage;

	private TestResult result;

	private TestAttemptStatus status;

	private Integer violationCount;

	private Integer attemptNumber;

	public Long getTestAttemptId() {
		return testAttemptId;
	}

	public void setTestAttemptId(Long testAttemptId) {
		this.testAttemptId = testAttemptId;
	}

	public Long getTestSetId() {
		return testSetId;
	}

	public void setTestSetId(Long testSetId) {
		this.testSetId = testSetId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getTrainingAssignmentId() {
		return trainingAssignmentId;
	}

	public void setTrainingAssignmentId(Long trainingAssignmentId) {
		this.trainingAssignmentId = trainingAssignmentId;
	}

	public LocalDateTime getStartTime() {
		return startTime;
	}

	public void setStartTime(LocalDateTime startTime) {
		this.startTime = startTime;
	}

	public LocalDateTime getEndTime() {
		return endTime;
	}

	public void setEndTime(LocalDateTime endTime) {
		this.endTime = endTime;
	}

	public BigDecimal getTotalScore() {
		return totalScore;
	}

	public void setTotalScore(BigDecimal totalScore) {
		this.totalScore = totalScore;
	}

	public Integer getTotalQuestions() {
		return totalQuestions;
	}

	public void setTotalQuestions(Integer totalQuestions) {
		this.totalQuestions = totalQuestions;
	}

	public Integer getCorrectAnswers() {
		return correctAnswers;
	}

	public void setCorrectAnswers(Integer correctAnswers) {
		this.correctAnswers = correctAnswers;
	}

	public Integer getWrongAnswers() {
		return wrongAnswers;
	}

	public void setWrongAnswers(Integer wrongAnswers) {
		this.wrongAnswers = wrongAnswers;
	}

	public Integer getSkippedQuestions() {
		return skippedQuestions;
	}

	public void setSkippedQuestions(Integer skippedQuestions) {
		this.skippedQuestions = skippedQuestions;
	}

	public BigDecimal getPassingPercentage() {
		return passingPercentage;
	}

	public void setPassingPercentage(BigDecimal passingPercentage) {
		this.passingPercentage = passingPercentage;
	}

	public BigDecimal getPercentage() {
		return percentage;
	}

	public void setPercentage(BigDecimal percentage) {
		this.percentage = percentage;
	}

	public TestResult getResult() {
		return result;
	}

	public void setResult(TestResult result) {
		this.result = result;
	}

	public TestAttemptStatus getStatus() {
		return status;
	}

	public void setStatus(TestAttemptStatus status) {
		this.status = status;
	}

	public Integer getViolationCount() {
		return violationCount;
	}

	public void setViolationCount(Integer violationCount) {
		this.violationCount = violationCount;
	}

	public Integer getAttemptNumber() {
		return attemptNumber;
	}

	public void setAttemptNumber(Integer attemptNumber) {
		this.attemptNumber = attemptNumber;
	}
}