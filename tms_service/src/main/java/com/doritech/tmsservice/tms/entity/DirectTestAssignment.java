package com.doritech.tmsservice.tms.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.doritech.tmsservice.enums.AssignmentStatus;

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
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "direct_test_assignments", uniqueConstraints = {
		@UniqueConstraint(name = "uk_direct_test_assignment_test_user", columnNames = { "test_set_id", "user_id" }) })
public class DirectTestAssignment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "direct_test_assignment_id")
	private Long directTestAssignmentId;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "test_set_id", nullable = false)
	private TestSet testSet;

	@Column(name = "user_id", nullable = false)
	private Long userId;

	@Column(name = "assigned_by", nullable = false)
	private Long assignedBy;

	@Column(name = "assigned_at", nullable = false)
	private LocalDateTime assignedAt;

	@Column(name = "due_date")
	private LocalDateTime dueDate;

	@Enumerated(EnumType.STRING)
	@Column(name = "status")
	private AssignmentStatus status = AssignmentStatus.NOT_STARTED;

	@Column(name = "progress_percentage", precision = 5, scale = 2)
	private BigDecimal progressPercentage = new BigDecimal("0.00");

	@Column(name = "completion_date")
	private LocalDateTime completionDate;

	@Column(name = "final_score", precision = 5, scale = 2)
	private BigDecimal finalScore;

	@Column(name = "is_passed")
	private Boolean isPassed = false;

	@Column(name = "attempted_questions")
	private Integer attemptedQuestions = 0;

	@Column(name = "correct_answers")
	private Integer correctAnswers = 0;

	@Column(name = "wrong_answers")
	private Integer wrongAnswers = 0;

	@Column(name = "total_questions")
	private Integer totalQuestions = 0;

	@Column(name = "remarks", columnDefinition = "TEXT")
	private String remarks;

	@Column(name = "started_at")
	private LocalDateTime startedAt;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "batch_id")
	private Batch batch;

	public Batch getBatch() {
		return batch;
	}

	public void setBatch(Batch batch) {
		this.batch = batch;
	}

	public Long getDirectTestAssignmentId() {
		return directTestAssignmentId;
	}

	public void setDirectTestAssignmentId(Long id) {
		this.directTestAssignmentId = id;
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

	public Long getAssignedBy() {
		return assignedBy;
	}

	public void setAssignedBy(Long assignedBy) {
		this.assignedBy = assignedBy;
	}

	public LocalDateTime getAssignedAt() {
		return assignedAt;
	}

	public void setAssignedAt(LocalDateTime assignedAt) {
		this.assignedAt = assignedAt;
	}

	public LocalDateTime getDueDate() {
		return dueDate;
	}

	public void setDueDate(LocalDateTime dueDate) {
		this.dueDate = dueDate;
	}

	public AssignmentStatus getStatus() {
		return status;
	}

	public void setStatus(AssignmentStatus status) {
		this.status = status;
	}

	public BigDecimal getProgressPercentage() {
		return progressPercentage;
	}

	public void setProgressPercentage(BigDecimal progressPercentage) {
		this.progressPercentage = progressPercentage;
	}

	public LocalDateTime getCompletionDate() {
		return completionDate;
	}

	public void setCompletionDate(LocalDateTime completionDate) {
		this.completionDate = completionDate;
	}

	public BigDecimal getFinalScore() {
		return finalScore;
	}

	public void setFinalScore(BigDecimal finalScore) {
		this.finalScore = finalScore;
	}

	public Boolean getIsPassed() {
		return isPassed;
	}

	public void setIsPassed(Boolean isPassed) {
		this.isPassed = isPassed;
	}

	public Integer getAttemptedQuestions() {
		return attemptedQuestions;
	}

	public void setAttemptedQuestions(Integer attemptedQuestions) {
		this.attemptedQuestions = attemptedQuestions;
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

	public Integer getTotalQuestions() {
		return totalQuestions;
	}

	public void setTotalQuestions(Integer totalQuestions) {
		this.totalQuestions = totalQuestions;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public LocalDateTime getStartedAt() {
		return startedAt;
	}

	public void setStartedAt(LocalDateTime startedAt) {
		this.startedAt = startedAt;
	}
}
