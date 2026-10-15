package com.doritech.tmsservice.response;

import java.time.LocalDateTime;
import java.util.List;

public class DocumentResponse {

	private Long documentId;
	private String documentName;
	private String documentDescription;
	private String documentUrl;
	private String documentType;
	private Long fileSizeBytes;
	private Boolean isSecure;
	private Long uploadedBy;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private List<Long> subProductIds;

	public Long getDocumentId() {
		return documentId;
	}

	public void setDocumentId(Long documentId) {
		this.documentId = documentId;
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

	public String getDocumentType() {
		return documentType;
	}

	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}

	public Long getFileSizeBytes() {
		return fileSizeBytes;
	}

	public void setFileSizeBytes(Long fileSizeBytes) {
		this.fileSizeBytes = fileSizeBytes;
	}

	public Boolean getIsSecure() {
		return isSecure;
	}

	public void setIsSecure(Boolean isSecure) {
		this.isSecure = isSecure;
	}

	public Long getUploadedBy() {
		return uploadedBy;
	}

	public void setUploadedBy(Long uploadedBy) {
		this.uploadedBy = uploadedBy;
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

	public List<Long> getSubProductIds() {
		return subProductIds;
	}

	public void setSubProductIds(List<Long> subProductIds) {
		this.subProductIds = subProductIds;
	}

	public DocumentResponse(Long documentId, String documentName, String documentDescription, String documentUrl,
			String documentType, Long fileSizeBytes, Boolean isSecure, Long uploadedBy, LocalDateTime createdAt,
			LocalDateTime updatedAt, List<Long> subProductIds) {
		super();
		this.documentId = documentId;
		this.documentName = documentName;
		this.documentDescription = documentDescription;
		this.documentUrl = documentUrl;
		this.documentType = documentType;
		this.fileSizeBytes = fileSizeBytes;
		this.isSecure = isSecure;
		this.uploadedBy = uploadedBy;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
		this.subProductIds = subProductIds;
	}

	public DocumentResponse() {
		super();
	}
}