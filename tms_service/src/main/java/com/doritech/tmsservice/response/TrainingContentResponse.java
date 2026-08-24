package com.doritech.tmsservice.response;

import java.time.LocalDateTime;

public class TrainingContentResponse {

    private Long trainingContentId;
    private Long trainingId;
    private String contentType;
    private Integer contentReferenceId;
    private String documentName;
    private String documentDescription;
    private String documentUrl;
    private Long fileSizeBytes;
    private Integer displayOrder;
    private Boolean isRequired;
    private LocalDateTime createdAt;

    public TrainingContentResponse() {
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "TrainingContentResponse [trainingContentId=" + trainingContentId
                + ", trainingId=" + trainingId + ", contentType=" + contentType + "]";
    }
}