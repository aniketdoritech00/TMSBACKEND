package com.doritech.tmsservice.response;

import java.time.LocalDateTime;

public class TrainingContentResponse {

	private Long trainingContentId;

	private Long trainingId;

	private String trainingCode;

	private String trainingName;

	private String contentType;

	private Long contentReferenceId;

	private Boolean isRequired;

	private Long createdBy;

	private Long updatedBy;

	private LocalDateTime createdAt;

	private LocalDateTime updatedAt;

	public Long getTrainingContentId() {
		return trainingContentId;
	}

	public void setTrainingContentId(Long trainingContentId) {
		this.trainingContentId = trainingContentId;
	}

	public Long getTrainingId() {
		return trainingId;
	}

	public void setTrainingId(Long trainingId) {
		this.trainingId = trainingId;
	}

	public String getTrainingCode() {
		return trainingCode;
	}

	public void setTrainingCode(String trainingCode) {
		this.trainingCode = trainingCode;
	}

	public String getTrainingName() {
		return trainingName;
	}

	public void setTrainingName(String trainingName) {
		this.trainingName = trainingName;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public Long getContentReferenceId() {
		return contentReferenceId;
	}

	public void setContentReferenceId(Long contentReferenceId) {
		this.contentReferenceId = contentReferenceId;
	}

	public Boolean getIsRequired() {
		return isRequired;
	}

	public void setIsRequired(Boolean isRequired) {
		this.isRequired = isRequired;
	}

	public Long getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(Long createdBy) {
		this.createdBy = createdBy;
	}

	public Long getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(Long updatedBy) {
		this.updatedBy = updatedBy;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}
}