package com.doritech.tmsservice.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class DocumentRequest {

	@NotNull(message = "{document.name.notnull}")
	@NotBlank(message = "{document.name.notblank}")
	@Size(min = 2, max = 255, message = "{document.name.size}")
	private String documentName;

	@Size(max = 2000, message = "{document.description.size}")
	private String documentDescription;

	@NotNull(message = "{document.url.notnull}")
	@NotBlank(message = "{document.url.notblank}")
	@Size(max = 500, message = "{document.url.size}")
	@Pattern(regexp = "^(https?://).+$", message = "{document.url.pattern}")
	private String documentUrl;

	@Size(max = 50, message = "{document.type.size}")
	private String documentType;

	@Min(value = 0, message = "{document.fileSizeBytes.min}")
	private Long fileSizeBytes;

	private Boolean isSecure;

	@NotNull(message = "{document.uploadedBy.notnull}")
	private Long uploadedBy;

	public DocumentRequest() {
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

	@Override
	public String toString() {
		return "DocumentRequest [documentName=" + documentName + ", documentType=" + documentType + ", isSecure="
				+ isSecure + ", uploadedBy=" + uploadedBy + "]";
	}
}