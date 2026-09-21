package com.doritech.tmsservice.tms.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "video_assessments")
public class VideoAssessment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "video_assessment_id")
    private Long videoAssessmentId;

    @Column(name = "training_id", nullable = false)
    private Long trainingId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "training_assignment_id")
    private Long trainingAssignmentId;

    @Column(name = "video_url", nullable = false, length = 500)
    private String videoUrl;

    @Column(name = "video_duration_seconds")
    private Integer videoDurationSeconds;

    @Column(name = "file_size_bytes")
    private Long fileSizeBytes;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private AssessmentStatus status = AssessmentStatus.SUBMITTED;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    @Column(name = "evaluated_at")
    private LocalDateTime evaluatedAt;

    @Column(name = "evaluated_by")
    private Long evaluatedBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "result")
    private AssessmentResult result;

    @Column(name = "evaluation_notes", columnDefinition = "TEXT")
    private String evaluationNotes;

    @Column(name = "score", precision = 5, scale = 2)
    private BigDecimal score;

    public enum AssessmentStatus {
        SUBMITTED,
        UNDER_REVIEW,
        PASSED,
        FAILED
    }

    public enum AssessmentResult {
        PASS,
        FAIL
    }

    public Long getVideoAssessmentId() {
        return videoAssessmentId;
    }

    public void setVideoAssessmentId(Long videoAssessmentId) {
        this.videoAssessmentId = videoAssessmentId;
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

    public Long getTrainingAssignmentId() {
        return trainingAssignmentId;
    }

    public void setTrainingAssignmentId(Long trainingAssignmentId) {
        this.trainingAssignmentId = trainingAssignmentId;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public Integer getVideoDurationSeconds() {
        return videoDurationSeconds;
    }

    public void setVideoDurationSeconds(Integer videoDurationSeconds) {
        this.videoDurationSeconds = videoDurationSeconds;
    }

    public Long getFileSizeBytes() {
        return fileSizeBytes;
    }

    public void setFileSizeBytes(Long fileSizeBytes) {
        this.fileSizeBytes = fileSizeBytes;
    }

    public AssessmentStatus getStatus() {
        return status;
    }

    public void setStatus(AssessmentStatus status) {
        this.status = status;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    public LocalDateTime getEvaluatedAt() {
        return evaluatedAt;
    }

    public void setEvaluatedAt(LocalDateTime evaluatedAt) {
        this.evaluatedAt = evaluatedAt;
    }

    public Long getEvaluatedBy() {
        return evaluatedBy;
    }

    public void setEvaluatedBy(Long evaluatedBy) {
        this.evaluatedBy = evaluatedBy;
    }

    public AssessmentResult getResult() {
        return result;
    }

    public void setResult(AssessmentResult result) {
        this.result = result;
    }

    public String getEvaluationNotes() {
        return evaluationNotes;
    }

    public void setEvaluationNotes(String evaluationNotes) {
        this.evaluationNotes = evaluationNotes;
    }

    public BigDecimal getScore() {
        return score;
    }

    public void setScore(BigDecimal score) {
        this.score = score;
    }
}