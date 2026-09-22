package com.doritech.tmsservice.response;

import java.time.LocalDateTime;

import com.doritech.tmsservice.enums.QuestionType;

public class VideoAssessmentQuestionResponse {

	private Long videoAssessmentQuestionId;

	private Long trainingId;

	private String trainingCode;

	private String trainingName;

	private String questionText;

	private QuestionType questionType;

	private LocalDateTime createdAt;

	public Long getVideoAssessmentQuestionId() {
		return videoAssessmentQuestionId;
	}

	public void setVideoAssessmentQuestionId(Long videoAssessmentQuestionId) {
		this.videoAssessmentQuestionId = videoAssessmentQuestionId;
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

	public String getQuestionText() {
		return questionText;
	}

	public void setQuestionText(String questionText) {
		this.questionText = questionText;
	}

	public QuestionType getQuestionType() {
		return questionType;
	}

	public void setQuestionType(QuestionType questionType) {
		this.questionType = questionType;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

}
