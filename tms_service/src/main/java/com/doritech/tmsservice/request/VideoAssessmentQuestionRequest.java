package com.doritech.tmsservice.request;

import com.doritech.tmsservice.enums.QuestionType;

public class VideoAssessmentQuestionRequest {

	private Long trainingId;

	private String questionText;

	private QuestionType questionType;

	public Long getTrainingId() {
		return trainingId;
	}

	public void setTrainingId(Long trainingId) {
		this.trainingId = trainingId;
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

}
