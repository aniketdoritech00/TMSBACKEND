package com.doritech.tmsservice.request;

import java.time.LocalDateTime;

public class TrainingAssignmentRequest {

	private Long trainingId;

	private Long userId;

	private Long batchId;

	private LocalDateTime dueDate;

	public TrainingAssignmentRequest() {
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

	public LocalDateTime getDueDate() {
		return dueDate;
	}

	public void setDueDate(LocalDateTime dueDate) {
		this.dueDate = dueDate;
	}

}