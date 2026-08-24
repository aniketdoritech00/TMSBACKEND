package com.doritech.tmsservice.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TrainingAssignmentResponse {

    private Long trainingAssignmentId;
    private Long trainingId;
    private Long userId;
    private Long batchId;
    private Long assignedBy;
    private LocalDateTime assignedAt;
    private LocalDateTime dueDate;
    private String status;
    private BigDecimal progressPercentage;
    private LocalDateTime completionDate;
    private BigDecimal finalScore;
    private Boolean isPassed;
    private Boolean certificateGenerated;
    private Integer attemptedQuestions;
    private Integer correctAnswers;
    private Integer wrongAnswers;
    private Integer totalQuestions;
    private String remarks;
    private LocalDateTime startedAt;

    public TrainingAssignmentResponse() {
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

    public Long getBatchId() {
        return batchId;
    }

    public void setBatchId(Long batchId) {
        this.batchId = batchId;
    }

    public Long getAssignedBy() {
        return assignedBy;
    }

    public void setAssignedBy(Long assignedBy) {
        this.assignedBy = assignedBy;
    }

    public LocalDateTime getAssignedAt() {
        return assignedAt;
    }

    public void setAssignedAt(LocalDateTime assignedAt) {
        this.assignedAt = assignedAt;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
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

    public LocalDateTime getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(LocalDateTime completionDate) {
        this.completionDate = completionDate;
    }

    public BigDecimal getFinalScore() {
        return finalScore;
    }

    public void setFinalScore(BigDecimal finalScore) {
        this.finalScore = finalScore;
    }

    public Boolean getIsPassed() {
        return isPassed;
    }

    public void setIsPassed(Boolean isPassed) {
        this.isPassed = isPassed;
    }

    public Boolean getCertificateGenerated() {
        return certificateGenerated;
    }

    public void setCertificateGenerated(Boolean certificateGenerated) {
        this.certificateGenerated = certificateGenerated;
    }

    public Integer getAttemptedQuestions() {
        return attemptedQuestions;
    }

    public void setAttemptedQuestions(Integer attemptedQuestions) {
        this.attemptedQuestions = attemptedQuestions;
    }

    public Integer getCorrectAnswers() {
        return correctAnswers;
    }

    public void setCorrectAnswers(Integer correctAnswers) {
        this.correctAnswers = correctAnswers;
    }

    public Integer getWrongAnswers() {
        return wrongAnswers;
    }

    public void setWrongAnswers(Integer wrongAnswers) {
        this.wrongAnswers = wrongAnswers;
    }

    public Integer getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(Integer totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }
}