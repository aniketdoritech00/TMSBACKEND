package com.doritech.tmsservice.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class TrainingRequest {

    @NotNull(message = "{training.code.notnull}")
    @NotBlank(message = "{training.code.notblank}")
    @Size(min = 2, max = 50, message = "{training.code.size}")
    @Pattern(regexp = "^[A-Z0-9_-]+$", message = "{training.code.pattern}")
    private String trainingCode;

    @NotNull(message = "{training.name.notnull}")
    @NotBlank(message = "{training.name.notblank}")
    @Size(min = 2, max = 255, message = "{training.name.size}")
    private String trainingName;

    @Size(max = 2000, message = "{training.description.size}")
    private String trainingDescription;

    @Pattern(regexp = "^(REGULAR|MANDATORY|REFRESHER)$", message = "{training.type.pattern}")
    private String trainingType;

    private Long trainingCategoryId;

    private Long trainerId;

    @Min(value = 1, message = "{training.durationDays.min}")
    private Integer trainingDurationDays;

    @DecimalMin(value = "0.0", message = "{training.passingPercentage.min}")
    @DecimalMax(value = "100.0", message = "{training.passingPercentage.max}")
    private BigDecimal passingPercentage;

    private Boolean isMandatory;

    private Long parentTrainingId;

    private Boolean hasAssessment;

    private Boolean hasVideoAssessment;

    @Pattern(regexp = "^(DRAFT|PUBLISHED|ARCHIVED)$", message = "{training.status.pattern}")
    private String status;

    @NotNull(message = "{training.createdBy.notnull}")
    private Long createdBy;

    public TrainingRequest() {
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

    @Override
    public String toString() {
        return "TrainingRequest [trainingCode=" + trainingCode + ", trainingName=" + trainingName
                + ", trainingType=" + trainingType + ", status=" + status + "]";
    }
}