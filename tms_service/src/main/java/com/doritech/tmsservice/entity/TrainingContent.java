package com.doritech.tmsservice.entity;

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

@Entity
@Table(name = "training_content")
public class TrainingContent {

	public enum ContentType {
		VIDEO, PDF, DOCUMENT, AUDIO, YOUTUBE
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "training_content_id")
	private Long trainingContentId;

	@Column(name = "training_id", nullable = false)
	private Long trainingId;

	@Enumerated(EnumType.STRING)
	@Column(name = "content_type", nullable = false)
	private ContentType contentType;

	@Column(name = "content_reference_id")
	private Integer contentReferenceId;

	@Column(name = "document_name", length = 255)
	private String documentName;

	@Column(name = "document_description", columnDefinition = "TEXT")
	private String documentDescription;

	@Column(name = "document_url", length = 500)
	private String documentUrl;

	@Column(name = "file_size_bytes")
	private Long fileSizeBytes;

	@Column(name = "display_order")
	private Integer displayOrder = 0;

	@Column(name = "is_required")
	private Boolean isRequired = true;

	@Column(name = "created_at", updatable = false)
	private LocalDateTime createdAt;

	public TrainingContent() {
	}

	@PrePersist
	protected void onCreate() {
		this.createdAt = LocalDateTime.now();
		if (this.displayOrder == null) {
			this.displayOrder = 0;
		}
		if (this.isRequired == null) {
			this.isRequired = true;
		}
	}

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

	public ContentType getContentType() {
		return contentType;
	}

	public void setContentType(ContentType contentType) {
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

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	@Override
	public String toString() {
		return "TrainingContent [trainingContentId=" + trainingContentId + ", trainingId=" + trainingId
				+ ", contentType=" + contentType + "]";
	}
}