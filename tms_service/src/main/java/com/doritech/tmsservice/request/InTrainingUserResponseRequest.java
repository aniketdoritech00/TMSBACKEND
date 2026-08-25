package com.doritech.tmsservice.request;

public class InTrainingUserResponseRequest {

	private Long questionId;

	private Long trainingAssignmentId;

	private String userAnswer;

	private Boolean isSkipped;

	private Integer timeTakenSeconds;

	public InTrainingUserResponseRequest() {
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
}