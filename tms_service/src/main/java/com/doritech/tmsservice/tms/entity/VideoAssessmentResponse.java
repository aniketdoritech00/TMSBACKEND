package com.doritech.tmsservice.tms.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.doritech.tmsservice.enums.ResponseStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "video_assessment_responses")
public class VideoAssessmentResponse {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "video_assessment_response_id")
	private Long videoAssessmentResponseId;

	@Column(name = "video_assessment_id", nullable = false)
	private Long videoAssessmentId;

	@Column(name = "video_assessment_question_id", nullable = false)
	private Long videoAssessmentQuestionId;

	@Column(name = "response_text", columnDefinition = "TEXT")
	private String responseText;

	@Column(name = "response_video_url", length = 500)
	private String responseVideoUrl;

	@Enumerated(EnumType.STRING)
	@Column(name = "status")
	private ResponseStatus status = ResponseStatus.PENDING;

	@Column(name = "score", precision = 5, scale = 2)
	private BigDecimal score;

	@Column(name = "evaluated_by")
	private Long evaluatedBy;

	@Column(name = "evaluated_at")
	private LocalDateTime evaluatedAt;

	public Long getVideoAssessmentResponseId() {
		return videoAssessmentResponseId;
	}

	public void setVideoAssessmentResponseId(Long videoAssessmentResponseId) {
		this.videoAssessmentResponseId = videoAssessmentResponseId;
	}

	public Long getVideoAssessmentId() {
		return videoAssessmentId;
	}

	public void setVideoAssessmentId(Long videoAssessmentId) {
		this.videoAssessmentId = videoAssessmentId;
	}

	public Long getVideoAssessmentQuestionId() {
		return videoAssessmentQuestionId;
	}

	public void setVideoAssessmentQuestionId(Long videoAssessmentQuestionId) {
		this.videoAssessmentQuestionId = videoAssessmentQuestionId;
	}

	public String getResponseText() {
		return responseText;
	}

	public void setResponseText(String responseText) {
		this.responseText = responseText;
	}

	public String getResponseVideoUrl() {
		return responseVideoUrl;
	}

	public void setResponseVideoUrl(String responseVideoUrl) {
		this.responseVideoUrl = responseVideoUrl;
	}

	public ResponseStatus getStatus() {
		return status;
	}

	public void setStatus(ResponseStatus status) {
		this.status = status;
	}

	public BigDecimal getScore() {
		return score;
	}

	public void setScore(BigDecimal score) {
		this.score = score;
	}

	public Long getEvaluatedBy() {
		return evaluatedBy;
	}

	public void setEvaluatedBy(Long evaluatedBy) {
		this.evaluatedBy = evaluatedBy;
	}

	public LocalDateTime getEvaluatedAt() {
		return evaluatedAt;
	}

	public void setEvaluatedAt(LocalDateTime evaluatedAt) {
		this.evaluatedAt = evaluatedAt;
	}

}