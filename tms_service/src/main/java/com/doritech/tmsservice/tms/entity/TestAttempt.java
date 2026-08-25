package com.doritech.tmsservice.tms.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.doritech.tmsservice.enums.TestAttemptStatus;
import com.doritech.tmsservice.enums.TestResult;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "test_attempts")
public class TestAttempt {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "test_attempt_id")
	private Long testAttemptId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "test_set_id", nullable = false)
	private TestSet testSet;

	private Long userId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "training_assignment_id")
	private TrainingAssignment trainingAssignment;

	@CreationTimestamp
	@Column(name = "start_time", updatable = false)
	private LocalDateTime startTime;

	@Column(name = "end_time")
	private LocalDateTime endTime;

	@Column(name = "total_score", columnDefinition = "decimal(10,2)")
	private BigDecimal totalScore;

	@Column(name = "total_questions", columnDefinition = "int default 0")
	private Integer totalQuestions = 0;

	@Column(name = "correct_answers", columnDefinition = "int default 0")
	private Integer correctAnswers = 0;

	@Column(name = "wrong_answers", columnDefinition = "int default 0")
	private Integer wrongAnswers = 0;

	@Column(name = "skipped_questions", columnDefinition = "int default 0")
	private Integer skippedQuestions = 0;

	@Column(name = "passing_percentage", columnDefinition = "decimal(5,2)")
	private BigDecimal passingPercentage;

	@Enumerated(EnumType.STRING)
	@Column(name = "result", columnDefinition = "enum('PASS','FAIL')")
	private TestResult result;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", columnDefinition = "enum('IN_PROGRESS','COMPLETED','ABANDONED') default 'IN_PROGRESS'")
	private TestAttemptStatus status = TestAttemptStatus.IN_PROGRESS;

	@Column(name = "violation_count", columnDefinition = "int default 0")
	private Integer violationCount = 0;

	@Column(name = "attempt_number", columnDefinition = "int default 1")
	private Integer attemptNumber = 1;

	public Long getTestAttemptId() {
		return testAttemptId;
	}

	public void setTestAttemptId(Long testAttemptId) {
		this.testAttemptId = testAttemptId;
	}

	public TestSet getTestSet() {
		return testSet;
	}

	public void setTestSet(TestSet testSet) {
		this.testSet = testSet;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public TrainingAssignment getTrainingAssignment() {
		return trainingAssignment;
	}

	public void setTrainingAssignment(TrainingAssignment trainingAssignment) {
		this.trainingAssignment = trainingAssignment;
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