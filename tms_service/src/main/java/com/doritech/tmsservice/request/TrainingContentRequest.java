package com.doritech.tmsservice.request;

public class TrainingContentRequest {

	private Long trainingId;

	private String contentType;

	private Long contentReferenceId;

	private Boolean isRequired;

	public Long getTrainingId() {
		return trainingId;
	}

	public void setTrainingId(Long trainingId) {
		this.trainingId = trainingId;
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
}