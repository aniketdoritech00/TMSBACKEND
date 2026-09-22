package com.doritech.tmsservice.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.doritech.tmsservice.enums.AssessmentResult;
import com.doritech.tmsservice.enums.AssessmentStatus;

public class VideoAssessmentResponse {

	private Long videoAssessmentId;

	private Long trainingId;

	private String trainingCode;

	private String trainingName;

	private Long userId;

	private Long trainingAssignmentId;

	private String videoUrl;

	private Integer videoDurationSeconds;

	private Long fileSizeBytes;

	private AssessmentStatus status;

	private LocalDateTime submittedAt;

	private LocalDateTime evaluatedAt;

	private Long evaluatedBy;

	private AssessmentResult result;

	private String evaluationNotes;

	private BigDecimal score;

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
