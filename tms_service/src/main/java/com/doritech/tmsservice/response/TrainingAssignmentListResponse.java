package com.doritech.tmsservice.response;

import java.math.BigDecimal;

public class TrainingAssignmentListResponse {

    private Long trainingAssignmentId;
    private Long trainingId;
    private Long userId;
    private String status;
    private BigDecimal progressPercentage;

    public TrainingAssignmentListResponse() {
    }

    public TrainingAssignmentListResponse(Long trainingAssignmentId, Long trainingId, Long userId,
            String status, BigDecimal progressPercentage) {
        this.trainingAssignmentId = trainingAssignmentId;
        this.trainingId = trainingId;
        this.userId = userId;
        this.status = status;
        this.progressPercentage = progressPercentage;
    }

    public Long getTrainingAssignmentId() {
        return trainingAssignmentId;
    }

    public void setTrainingAssignmentId(Long trainingAssignmentId) {
        this.trainingAssignmentId = trainingAssignmentId;
    }

    public Long getTrainingId() {
        return trainingId;
    }

    public void setTrainingId(Long trainingId) {
        this.trainingId = trainingId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getProgressPercentage() {
        return progressPercentage;
    }

    public void setProgressPercentage(BigDecimal progressPercentage) {
        this.progressPercentage = progressPercentage;
    }
}