package com.doritech.tmsservice.tms.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.doritech.tmsservice.enums.AssessmentResult;
import com.doritech.tmsservice.enums.AssessmentStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "video_assessments", indexes = {
		@Index(name = "idx_video_assessment_training", columnList = "training_id"),
		@Index(name = "idx_video_assessment_user", columnList = "user_id"),
		@Index(name = "idx_video_assessment_assignment", columnList = "training_assignment_id"),
		@Index(name = "idx_video_assessment_status", columnList = "status"),
		@Index(name = "idx_video_assessment_submitted", columnList = "submitted_at") })
public class VideoAssessment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "video_assessment_id")
	private Long videoAssessmentId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "training_id", nullable = false)
	private Training training;

	@Column(name = "user_id", nullable = false)
	private Long userId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "training_assignment_id", nullable = false)
	private TrainingAssignment trainingAssignment;

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

	public Long getVideoAssessmentId() {
		return videoAssessmentId;
	}

	public void setVideoAssessmentId(Long videoAssessmentId) {
		this.videoAssessmentId = videoAssessmentId;
	}

	public Training getTraining() {
		return training;
	}

	public void setTraining(Training training) {
		this.training = training;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public TrainingAssignment getTrainingAssignment() {
		return trainingAssignment;
	}

	public void setTrainingAssignment(TrainingAssignment trainingAssignment) {
		this.trainingAssignment = trainingAssignment;
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
