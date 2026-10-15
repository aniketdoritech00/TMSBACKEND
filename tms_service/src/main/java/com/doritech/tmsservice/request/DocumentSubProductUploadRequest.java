package com.doritech.tmsservice.request;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public class DocumentSubProductUploadRequest {

    @NotBlank(message = "Document name is required")
    private String documentName;

    private String documentDescription;

    @NotEmpty(message = "At least one sub product is required")
    private List<Long> subProductIds;
    
    private boolean isSecure;

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

    public List<Long> getSubProductIds() {
        return subProductIds;
    }

    public void setSubProductIds(List<Long> subProductIds) {
        this.subProductIds = subProductIds;
    }

	public boolean isSecure() {
		return isSecure;
	}

	public void setSecure(boolean isSecure) {
		this.isSecure = isSecure;
	}
    
}