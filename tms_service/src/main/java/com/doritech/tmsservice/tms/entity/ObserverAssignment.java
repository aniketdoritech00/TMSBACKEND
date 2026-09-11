package com.doritech.tmsservice.tms.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "observer_assignments", uniqueConstraints = {
		@UniqueConstraint(name = "uk_observer_assignment", columnNames = { "user_id", "training_assignment_id" }) })
public class ObserverAssignment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "observer_assignment_id")
	private Long observerAssignmentId;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "training_assignment_id", nullable = false)
	private TrainingAssignment trainingAssignment;

	@Column(name = "user_id", nullable = false)
	private Long userId;

	@Column(name = "created_at")
	private LocalDateTime createdAt;

	@Column(name = "created_by")
	private Long createdBy;

	public ObserverAssignment() {
	}

	public Long getObserverAssignmentId() {
		return observerAssignmentId;
	}

	public void setObserverAssignmentId(Long observerAssignmentId) {
		this.observerAssignmentId = observerAssignmentId;
	}

	public TrainingAssignment getTrainingAssignment() {
		return trainingAssignment;
	}

	public void setTrainingAssignment(TrainingAssignment trainingAssignment) {
		this.trainingAssignment = trainingAssignment;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public Long getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(Long createdBy) {
		this.createdBy = createdBy;
	}
}
