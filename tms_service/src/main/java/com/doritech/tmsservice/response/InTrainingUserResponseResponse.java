package com.doritech.tmsservice.response;

import java.time.LocalDateTime;

public class InTrainingUserResponseResponse {

	private Long inTrainingUserResponseId;

	private Long questionId;

	private Long trainingAssignmentId;

	private String userAnswer;

	private Boolean isCorrect;

	private Boolean isSkipped;

	private Integer timeTakenSeconds;

	private LocalDateTime respondedAt;
	
	
	

	public InTrainingUserResponseResponse() {
	}

	public Long getInTrainingUserResponseId() {
		return inTrainingUserResponseId;
	}

	public void setInTrainingUserResponseId(Long inTrainingUserResponseId) {
		this.inTrainingUserResponseId = inTrainingUserResponseId;
	}

	public Long getQuestionId() {
		return questionId;
	}

	public void setQuestionId(Long questionId) {
		this.questionId = questionId;
	}

	public Long getTrainingAssignmentId() {
		return trainingAssignmentId;
	}

	public void setTrainingAssignmentId(Long trainingAssignmentId) {
		this.trainingAssignmentId = trainingAssignmentId;
	}

	public String getUserAnswer() {
		return userAnswer;
	}

	public void setUserAnswer(String userAnswer) {
		this.userAnswer = userAnswer;
	}

	public Boolean getIsCorrect() {
		return isCorrect;
	}

	public void setIsCorrect(Boolean isCorrect) {
		this.isCorrect = isCorrect;
	}

	public Boolean getIsSkipped() {
		return isSkipped;
	}

	public void setIsSkipped(Boolean isSkipped) {
		this.isSkipped = isSkipped;
	}

	public Integer getTimeTakenSeconds() {
		return timeTakenSeconds;
	}

	public void setTimeTakenSeconds(Integer timeTakenSeconds) {
		this.timeTakenSeconds = timeTakenSeconds;
	}

	public LocalDateTime getRespondedAt() {
		return respondedAt;
	}

	public void setRespondedAt(LocalDateTime respondedAt) {
		this.respondedAt = respondedAt;
	}
}