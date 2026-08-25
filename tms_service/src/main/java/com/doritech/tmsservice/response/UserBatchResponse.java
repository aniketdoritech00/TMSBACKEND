package com.doritech.tmsservice.response;

import java.time.LocalDateTime;

public class UserBatchResponse {

    private Long userBatchId;
    private Long userId;
    private Long batchId;
    private LocalDateTime assignedAt;
    private Long assignedBy;

    public Long getUserBatchId() {
        return userBatchId;
    }

    public void setUserBatchId(Long userBatchId) {
        this.userBatchId = userBatchId;
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
}