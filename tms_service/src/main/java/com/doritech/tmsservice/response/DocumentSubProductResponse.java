package com.doritech.tmsservice.response;

import java.time.LocalDateTime;

public class DocumentSubProductResponse {

    private Long documentId;
    private Long subProductId;
    private LocalDateTime assignedAt;
    private Long assignedBy;

    public DocumentSubProductResponse() {
    }

    public DocumentSubProductResponse(Long documentId, Long subProductId, LocalDateTime assignedAt, Long assignedBy) {
        this.documentId = documentId;
        this.subProductId = subProductId;
        this.assignedAt = assignedAt;
        this.assignedBy = assignedBy;
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

    public LocalDateTime getAssignedAt() {
        return assignedAt;
    }

    public void setAssignedAt(LocalDateTime assignedAt) {
        this.assignedAt = assignedAt;
    }

    public Long getAssignedBy() {
        return assignedBy;
    }

    public void setAssignedBy(Long assignedBy) {
        this.assignedBy = assignedBy;
    }

    @Override
    public String toString() {
        return "DocumentSubProductResponse [documentId=" + documentId
                + ", subProductId=" + subProductId + "]";
    }
}