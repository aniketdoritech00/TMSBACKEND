package com.doritech.tmsservice.request;

import jakarta.validation.constraints.NotNull;

public class DocumentSubProductRequest {

    @NotNull(message = "{documentSubProduct.documentId.notnull}")
    private Long documentId;

    @NotNull(message = "{documentSubProduct.subProductId.notnull}")
    private Long subProductId;

    private Long assignedBy;

    public DocumentSubProductRequest() {
    }

    public Long getDocumentId() {
        return documentId;
    }

    public void setDocumentId(Long documentId) {
        this.documentId = documentId;
    }

    public Long getSubProductId() {
        return subProductId;
    }

    public void setSubProductId(Long subProductId) {
        this.subProductId = subProductId;
    }

    public Long getAssignedBy() {
        return assignedBy;
    }

    public void setAssignedBy(Long assignedBy) {
        this.assignedBy = assignedBy;
    }

    @Override
    public String toString() {
        return "DocumentSubProductRequest [documentId=" + documentId
                + ", subProductId=" + subProductId + ", assignedBy=" + assignedBy + "]";
    }
}