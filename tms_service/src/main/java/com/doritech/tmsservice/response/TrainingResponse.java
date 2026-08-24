package com.doritech.tmsservice.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TrainingResponse {

    private Long trainingId;
    private String trainingCode;
    private String trainingName;
    private String trainingDescription;
    private String trainingType;
    private Long trainingCategoryId;
    private Long trainerId;
    private Integer trainingDurationDays;
    private BigDecimal passingPercentage;
    private Boolean isMandatory;
    private Long parentTrainingId;
    private Boolean hasAssessment;
    private Boolean hasVideoAssessment;
    private String status;
    private Long createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime publishedAt;

    public TrainingResponse() {
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

    public String getTrainingDescription() {
        return trainingDescription;
    }

    public void setTrainingDescription(String trainingDescription) {
        this.trainingDescription = trainingDescription;
    }

    public String getTrainingType() {
        return trainingType;
    }

    public void setTrainingType(String trainingType) {
        this.trainingType = trainingType;
    }

    public Long getTrainingCategoryId() {
        return trainingCategoryId;
    }

    public void setTrainingCategoryId(Long trainingCategoryId) {
        this.trainingCategoryId = trainingCategoryId;
    }

    public Long getTrainerId() {
        return trainerId;
    }

    public void setTrainerId(Long trainerId) {
        this.trainerId = trainerId;
    }

    public Integer getTrainingDurationDays() {
        return trainingDurationDays;
    }

    public void setTrainingDurationDays(Integer trainingDurationDays) {
        this.trainingDurationDays = trainingDurationDays;
    }

    public BigDecimal getPassingPercentage() {
        return passingPercentage;
    }

    public void setPassingPercentage(BigDecimal passingPercentage) {
        this.passingPercentage = passingPercentage;
    }

    public Boolean getIsMandatory() {
        return isMandatory;
    }

    public void setIsMandatory(Boolean isMandatory) {
        this.isMandatory = isMandatory;
    }

    public Long getParentTrainingId() {
        return parentTrainingId;
    }

    public void setParentTrainingId(Long parentTrainingId) {
        this.parentTrainingId = parentTrainingId;
    }

    public Boolean getHasAssessment() {
        return hasAssessment;
    }

    public void setHasAssessment(Boolean hasAssessment) {
        this.hasAssessment = hasAssessment;
    }

    public Boolean getHasVideoAssessment() {
        return hasVideoAssessment;
    }

    public void setHasVideoAssessment(Boolean hasVideoAssessment) {
        this.hasVideoAssessment = hasVideoAssessment;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
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

    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(LocalDateTime publishedAt) {
        this.publishedAt = publishedAt;
    }

    @Override
    public String toString() {
        return "TrainingResponse [trainingId=" + trainingId + ", trainingCode=" + trainingCode
                + ", trainingName=" + trainingName + ", status=" + status + "]";
    }
}