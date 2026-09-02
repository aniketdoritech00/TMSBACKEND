package com.doritech.tmsservice.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class TrainingSubProductRequest {

	@NotNull(message = "Training ID is required")
	@Positive(message = "Training ID must be greater than 0")
	private Long trainingId;

	@NotNull(message = "Sub product ID is required")
	@Positive(message = "Sub product ID must be greater than 0")
	private Long subProductId;

	public Long getTrainingId() {
		return trainingId;
	}

	public void setTrainingId(Long trainingId) {
		this.trainingId = trainingId;
	}

	public Long getSubProductId() {
		return subProductId;
	}

	public void setSubProductId(Long subProductId) {
		this.subProductId = subProductId;
	}
}