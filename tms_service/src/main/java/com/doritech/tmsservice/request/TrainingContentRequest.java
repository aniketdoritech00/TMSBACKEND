package com.doritech.tmsservice.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class TrainingContentRequest {

	@NotNull(message = "{trainingContent.trainingId.notnull}")
	private Long trainingId;

	@NotNull(message = "{trainingContent.contentType.notnull}")
	@Pattern(regexp = "^(VIDEO|PDF|DOCUMENT|AUDIO|YOUTUBE)$", message = "{trainingContent.contentType.pattern}")
	private String contentType;

	private Integer contentReferenceId;

	@Size(max = 255, message = "{trainingContent.documentName.size}")
	private String documentName;

	@Size(max = 2000, message = "{trainingContent.documentDescription.size}")
	private String documentDescription;

	@Size(max = 500, message = "{trainingContent.documentUrl.size}")
	@Pattern(regexp = "^(https?://).+$", message = "{trainingContent.documentUrl.pattern}")
	private String documentUrl;

	@Min(value = 0, message = "{trainingContent.fileSizeBytes.min}")
	private Long fileSizeBytes;

	@Min(value = 0, message = "{trainingContent.displayOrder.min}")
	private Integer displayOrder;

	private Boolean isRequired;

	public TrainingContentRequest() {
	}

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

	public Integer getContentReferenceId() {
		return contentReferenceId;
	}

	public void setContentReferenceId(Integer contentReferenceId) {
		this.contentReferenceId = contentReferenceId;
	}

	public String getDocumentName() {
		return documentName;
	}

	public void setDocumentName(String documentName) {
		this.documentName = documentName;
	}

	public String getDocumentDescription() {
		return documentDescription;
	}

	public void setDocumentDescription(String documentDescription) {
		this.documentDescription = documentDescription;
	}

	public String getDocumentUrl() {
		return documentUrl;
	}

	public void setDocumentUrl(String documentUrl) {
		this.documentUrl = documentUrl;
	}

	public Long getFileSizeBytes() {
		return fileSizeBytes;
	}

	public void setFileSizeBytes(Long fileSizeBytes) {
		this.fileSizeBytes = fileSizeBytes;
	}

	public Integer getDisplayOrder() {
		return displayOrder;
	}

	public void setDisplayOrder(Integer displayOrder) {
		this.displayOrder = displayOrder;
	}

	public Boolean getIsRequired() {
		return isRequired;
	}

	public void setIsRequired(Boolean isRequired) {
		this.isRequired = isRequired;
	}

	@Override
	public String toString() {
		return "TrainingContentRequest [trainingId=" + trainingId + ", contentType=" + contentType
				+ ", contentReferenceId=" + contentReferenceId + "]";
	}
}