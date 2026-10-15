package com.doritech.tmsservice.response;

import java.time.LocalDateTime;

public class ObserverAssignmentResponse {

	private Long observerAssignmentId;
	private Long trainingAssignmentId;
	private Long userId;
	private LocalDateTime createdAt;
	private Long createdBy;

	public Long getObserverAssignmentId() {
		return observerAssignmentId;
	}

	public void setObserverAssignmentId(Long observerAssignmentId) {
		this.observerAssignmentId = observerAssignmentId;
	}

	public Long getTrainingAssignmentId() {
		return trainingAssignmentId;
	}

	public void setTrainingAssignmentId(Long trainingAssignmentId) {
		this.trainingAssignmentId = trainingAssignmentId;
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
