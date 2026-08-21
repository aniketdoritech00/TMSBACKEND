package com.doritech.tmsservice.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "training_assignments", uniqueConstraints = @UniqueConstraint(columnNames = { "training_id", "user_id" }))
public class TrainingAssignment {

	public enum Status {
		NOT_STARTED, IN_PROGRESS, COMPLETED, OVERDUE, EXPIRED
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "training_assignment_id")
	private Long trainingAssignmentId;

	@Column(name = "training_id", nullable = false)
	private Long trainingId;

	@Column(name = "user_id", nullable = false)
	private Long userId;

	@Column(name = "batch_id")
	private Long batchId;

	@Column(name = "assigned_by", nullable = false)
	private Long assignedBy;

	@Column(name = "assigned_at")
	private LocalDateTime assignedAt;

	@Column(name = "due_date")
	private LocalDateTime dueDate;

	@Enumerated(EnumType.STRING)
	@Column(name = "status")
	private Status status = Status.NOT_STARTED;

	@Column(name = "progress_percentage", precision = 5, scale = 2)
	private BigDecimal progressPercentage = BigDecimal.ZERO;

	@Column(name = "completion_date")
	private LocalDateTime completionDate;

	@Column(name = "final_score", precision = 5, scale = 2)
	private BigDecimal finalScore;

	@Column(name = "is_passed")
	private Boolean isPassed = false;

	@Column(name = "certificate_generated")
	private Boolean certificateGenerated = false;

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

	public TrainingAssignment() {
	}

	@PrePersist
	protected void onCreate() {
		this.assignedAt = LocalDateTime.now();
		if (this.status == null) {
			this.status = Status.NOT_STARTED;
		}
		if (this.progressPercentage == null) {
			this.progressPercentage = BigDecimal.ZERO;
		}
		if (this.isPassed == null) {
			this.isPassed = false;
		}
		if (this.certificateGenerated == null) {
			this.certificateGenerated = false;
		}
		if (this.attemptedQuestions == null) {
			this.attemptedQuestions = 0;
		}
		if (this.correctAnswers == null) {
			this.correctAnswers = 0;
		}
		if (this.wrongAnswers == null) {
			this.wrongAnswers = 0;
		}
		if (this.totalQuestions == null) {
			this.totalQuestions = 0;
		}
	}

	public Long getTrainingAssignmentId() {
		return trainingAssignmentId;
	}

	public void setTrainingAssignmentId(Long trainingAssignmentId) {
		this.trainingAssignmentId = trainingAssignmentId;
	}

	public Long getTrainingId() {
		return trainingId;
	}

	public void setTrainingId(Long trainingId) {
		this.trainingId = trainingId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getBatchId() {
		return batchId;
	}

	public void setBatchId(Long batchId) {
		this.batchId = batchId;
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

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
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

	public Boolean getCertificateGenerated() {
		return certificateGenerated;
	}

	public void setCertificateGenerated(Boolean certificateGenerated) {
		this.certificateGenerated = certificateGenerated;
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