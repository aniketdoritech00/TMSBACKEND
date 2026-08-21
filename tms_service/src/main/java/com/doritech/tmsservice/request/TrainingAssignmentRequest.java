package com.doritech.tmsservice.request;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;

public class TrainingAssignmentRequest {

    @NotNull(message = "{trainingAssignment.trainingId.notnull}")
    private Long trainingId;

    @NotNull(message = "{trainingAssignment.userId.notnull}")
    private Long userId;

    private Long batchId;

    private LocalDateTime dueDate;

    public TrainingAssignmentRequest() {
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

    public Long getBatchId() {
        return batchId;
    }

    public void setBatchId(Long batchId) {
        this.batchId = batchId;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    @Override
    public String toString() {
        return "TrainingAssignmentRequest [trainingId=" + trainingId + ", userId=" + userId + "]";
    }
}